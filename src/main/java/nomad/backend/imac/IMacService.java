package nomad.backend.imac;

import lombok.RequiredArgsConstructor;
import nomad.backend.admin.CredentialsService;
import nomad.backend.global.Define;
import nomad.backend.global.api.ApiService;
import nomad.backend.global.api.mapper.Cluster;
import nomad.backend.global.exception.custom.NotFoundException;
import nomad.backend.history.HistoryService;
import nomad.backend.slack.SlackService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@EnableScheduling
public class IMacService {
    private final IMacRepository iMacRepository;
    private final ApiService apiService;
    private final CredentialsService credentialsService;
    private final SlackService slackService;
    private final HistoryService historyService;

    @Transactional
    public Double getClusterDensity(String cluster) {
        DensityInfo population = new DensityInfo();
        List<IMac> iMacList = iMacRepository.findLoginCadetByCluster(cluster);
        double density = iMacList.size() / population.getClusters().get(cluster) * 10;
        return Math.round(density) / 10.0;
    }

    @Transactional
    public List<IMacDto> getClusterInfo(String cluster) {
        List<IMac> iMacList = iMacRepository.findByCluster(cluster);
        if (iMacList == null)
            throw new NotFoundException();
        return parseIMacList(iMacList);
    }

    public IMacDto parseIMac(IMac iMac) {
        return toIMacDto(iMac);
    }

    public List<IMacDto> parseIMacList(List<IMac> iMacList) {
        return iMacList.stream()
                .map(iMac -> toIMacDto(iMac))
                .collect(Collectors.toList());
    }

    // imac getCadet이 있으면 location, status = true, elapsedTime = -1
    // imac getCadet이 없으면 , status = false, getElapsedgotjme이 42분 지났으면 reset하고 -1
    // 42분이 안지났으면 elapsedTime 주기
    private IMacDto toIMacDto(IMac iMac) {
        int elapsedTime = getElapsedTime(iMac.getCadet(), iMac.getLogoutTime());
        boolean isAvailable = iMac.getCadet() == null;
        if (!isAvailable || elapsedTime < 43)
            return new IMacDto(iMac.getLocation(), iMac.getCadet(), isAvailable, elapsedTime);
        else {
            iMac.resetLogoutTime();
            return new IMacDto(iMac.getLocation(), iMac.getCadet(), isAvailable, -1);
        }
    }

    private int getElapsedTime(String isEmpty, Date logoutTime) {
        if (isEmpty != null || logoutTime == null)
            return -1;
        return (int) (new Date().getTime() - logoutTime.getTime()) / (1000 * 60);
    }

    public IMac findByLocation(String location) {
        return iMacRepository.findByLocation(location);
    }

    @Transactional
    public void updateAllInClusterCadet() {
        int page = 1;
        Date now = new Date();
        System.out.println("incluster 시작");
        while (true) {
            List<Cluster> clusterCadets = apiService.getAllLoginCadets(credentialsService.getAccessToken(), page);
            for (Cluster info : clusterCadets) {
                System.out.println("incluster = " + info.getUser().getLocation() + ", " + info.getHost());
                String location = info.getUser().getLocation();
                historyService.addHistory(location, info.getUser().getLogin(), info.getBegin_at());
                if (!info.getHost().equalsIgnoreCase(location))
                    break;
                IMac iMac = iMacRepository.findByLocation(location);
                if (iMac == null)
                    continue;
                // 디비에서 저장하고 있던 아이맥자리의 cadet과 현재 로그인 중 좌석의 카뎃이 같다면 한 번 추적되었고 알림이 나간 것으로 판단
                // 굳이 begin_at까지 보지 않는 것은 같은 자리, 같은 카뎃이면.. 로그아웃했다 로그인해도 빠른 시간내에 했을 텐데.. 굳이 해야 할까 싶어서..?
                // 로그인 | 서버 재시작 (로그아웃, 로그인) | 로그인 이상태라면 원래 서버가 중단되는 동안 나갔어여할 아웃, 인 알람이 안나갔다는 것
                // 근데 꺼지기 전에도 로그인이었고, 켜진 후에 로그인이면 중간 아웃 -> 인이 전부 알람이 안나가니까 굳이 인에 대해 알람을 더 줄 필요가 없다.
                // 그럴경우에는 updateAt만 바꿔줘서 밑에 강제로그아웃처리 당하지 않게 해줌
                // 그렇지 않고 로그인 유저가 변경되면 추적을 놓친 자리가 있다고 판단하여 자리 업뎃하고 슬랙 알람도 줌
                if (info.getUser().getLogin().equalsIgnoreCase(iMac.getCadet())) {
                    iMac.updateLoginCadet(now);
                    System.out.println("(시간만 업데이트) user = " +  info.getUser().getLogin() + " 자리 = " + info.getHost());
                }
                else {
                    Instant instant = Instant.parse(info.getBegin_at());
                    iMac.updateLoginCadet(info.getUser().getLogin(), now, new Date(instant.toEpochMilli()));
                    slackService.findIMacNotificationAndSendMessage(info.getUser().getLogin(), location,
                            Define.TAKEN_SEAT + "(서버 재시작으로 인하여 실제 자리 사용 시작 시간보다 알람이 지연 발송됐을 수 있습니다. 양해 부탁드립니다.");
                    System.out.println("(전부 업데이트) user = " +  info.getUser().getLogin() + " 자리 = " + info.getHost());
                }
            }
            if (clusterCadets.get(99).getEnd_at() != null)
                break;
            page++;
        }
        List<IMac> needToLogoutIMacs = iMacRepository.findByCadetAndUpdatedAt(now);
        // 서버가 재시작되는 동안 로그인이었던 자리가 로그아웃으로 바껴서 원래 나가야 하던 타이밍에 알림을 못주고 강제로 정리하는 과정에서 알림을 준다.
        // 이 자리에 대한 슬랙서비스는 누가 나갔는지를 알 수 없음. 기존 정보에서 누군가 들어왔다 다시 나갔을수도 있으니까. 그래서 내가 로그아웃한 거라도 알림을 받게 됨.
        // 그런데 만약 서버가 아주 빨리 재시작되어 1분마다 도는 메소드에서 해당 정보를 가져와 겹치는 경우 logoutTime이 갱신되면서 다시 또 알람이 나갈 수 있다.
        // 로그인 | 서버 꺼짐 (로그아웃) | 로그아웃 상태인 경우
        needToLogoutIMacs.forEach(imac -> {
                    imac.forceLogout();
                    slackService.findIMacNotificationAndSendMessage(null, imac.getLocation(),
                            Define.EMPTY_SEAT + "(서버 재시작으로 인하여 본인이 로그아웃 한 자리 또는 중복 알림일 수 있습니다. 양해 부탁드립니다.)");});
    }

    //백그라운드 3분마다 돌 것인지?? 1분..?
    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    // 테스트할때는 한 5분 간격? 그리고 디비 동시성 문제 확인
    @Transactional
    public void update1minClusterInfo(){
        System.out.println("method - update1minClusterInfo");
        String accessToken = credentialsService.getAccessToken();
        int page = 1;

        while (true) {
            List<Cluster> logoutCadets = apiService.getRecentlyLogoutCadet(accessToken, page);
            for (Cluster info : logoutCadets) {
                IMac iMac = iMacRepository.findByLocation(info.getHost());
                if (iMac == null)
                    continue;
                Date logoutTime = new Date(Instant.parse(info.getEnd_at()).toEpochMilli());
                if (iMac.getLogoutTime() != null && !iMac.getLogoutTime().before(logoutTime))
                    continue;
                // 중복된 정보가 넘어올 때 가장 최근의 logoutTime이 아니라면 해당 자리에 대해 업데이트와 알림 모두 보내지 않는다.
                // 같은 정보에 대해 3분 조건으로 인해 3번이 들어와도 똑같이 처리가 가능한가? 이게 맞나?
                iMac.updateLogoutCadet(logoutTime, info.getUser().getLogin());
                slackService.findIMacNotificationAndSendMessage(info.getUser().getLogin(), iMac.getLocation(), Define.EMPTY_SEAT);
                System.out.println("logout = " + info.getHost() + ", cadet = " + info.getUser().getLogin());
            }
            if (logoutCadets.size() < 50)
                break;
            page++;
        }
        System.out.println("logout 끝, login 시작");
        page = 1;
        while(true) {
            List<Cluster> loginCadets = apiService.getRecentlyLoginCadet(accessToken, page);
            for (Cluster info : loginCadets) {
                IMac iMac = iMacRepository.findByLocation(info.getHost());
                if (iMac != null && info.getHost().equalsIgnoreCase(info.getUser().getLocation())) {
                    historyService.addHistory(iMac.getLocation(), info.getUser().getLogin(), info.getBegin_at());
                    Date loginTime = new Date(Instant.parse(info.getBegin_at()).toEpochMilli());
                    // 최신순부터 오기 떄문에 다음게 같은 자리에 옛날 beginAt이라면 처리하지 않는다.
                    // 히스토리도 이 안으로 들어오는 지 아닌지 잘 모르겠음 확인 부탁.
                    if (iMac.getLoginTime() != null && !iMac.getLoginTime().before(loginTime))
                        continue;
                    iMac.updateLoginCadet(info.getUser().getLogin(), null, loginTime);
                    slackService.findIMacNotificationAndSendMessage(info.getUser().getLogin(), info.getHost(), Define.TAKEN_SEAT);
                    System.out.println("login = " + info.getHost() + ", intra = " + info.getUser().getLogin());
                }
            }
            if (loginCadets.size() < 50)
                break;
            page++;
        }
    }

    @Transactional
    public void loadCsvDataToDatabase() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("/home/ec2-user/apps/backend/src/main/java/nomad/backend/imac/imac.csv", Charset.forName("UTF-8")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                IMac iMac = new IMac(data[0], data[1]);
                iMacRepository.save(iMac);
            }
        }
    }
}