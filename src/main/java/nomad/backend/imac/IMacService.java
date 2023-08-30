package nomad.backend.imac;

import lombok.RequiredArgsConstructor;
import nomad.backend.admin.CredentialsService;
import nomad.backend.global.Define;
import nomad.backend.global.api.ApiService;
import nomad.backend.global.api.mapper.Cluster;
import nomad.backend.global.exception.custom.NotFoundException;
import nomad.backend.history.HistoryService;
import nomad.backend.member.Member;
import nomad.backend.member.MemberService;
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
        while (true) {
            List<Cluster> clusterCadets = apiService.getAllLoginCadets(credentialsService.getAccessToken(), page);
            for (Cluster info : clusterCadets) {
                String location = info.getUser().getLocation();
                historyService.addHistory(location, info.getUser().getLogin(), info.getBegin_at());
                if (!info.getHost().equalsIgnoreCase(location))
                    break;
                IMac iMac = iMacRepository.findByLocation(location);
                if (iMac == null)
                    continue;
                System.out.println("user = " +  info.getUser().getLogin() + " 자리 = " + info.getHost());
                iMac.updateLoginCadet(info.getUser().getLogin(), now);

                // 이 경우도 알림이 가는게 맞나..? 이전 알림 여부와 관계없이 우다다다 나갈텐데..
                slackService.findNotificationAndSendMessage(info.getUser().getLogin(), location, Define.TAKEN_SEAT);

                // 사용하고 있는 유저가 멤버면 history에 기록을 해줘야 하는데
                // 이 메소드의 경우 서버가 꺼졌다 켜질때만 되는 거임.
                // history에서 당일 같은 자리에 대한 중복검증?
            }
            if (clusterCadets.get(99).getEnd_at() != null)
                break;
            page++;
        }
        List<IMac> needToLogoutIMacs = iMacRepository.findByCadetAndUpdatedAt(now);
        // 이 자리에 대해서도 슬랙 서비스가 필요할지..?
        needToLogoutIMacs.stream() // 의도대로 되는 지 테스트 꼭 꼭 필요
                .filter(Objects::nonNull)
                .forEach(IMac::forceLogout);
    }

    //백그라운드 3분마다 돌 것인지?? 1분..?
    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    // 테스트할때는 한 5분 간격? 그리고 디비 동시성 문제 확인
    @Transactional
    public void update3minClusterInfo(){
        System.out.println("method - update3minClusterInfo");
        String accessToken = credentialsService.getAccessToken();
        int page = 1;

        while (true) {
            List<Cluster> logoutCadets = apiService.getRecentlyLogoutCadet(accessToken, page);
            for (Cluster info : logoutCadets) {
                IMac iMac = iMacRepository.findByLocation(info.getHost());
                if (iMac == null) {
                    continue;
                }
                Instant instant = Instant.parse(info.getEnd_at());
                iMac.updateLogoutCadet(new Date(instant.toEpochMilli()), info.getUser().getLogin());
                slackService.findNotificationAndSendMessage(info.getUser().getLogin(), iMac.getLocation(), Define.TAKEN_SEAT);
//                slackService.findNotificationAndSendMessage(info.getUser().getLogin(), info.getHost(), Define.EMPTY_SEAT);
                // 같은 자리가 여러번 로그아웃 되는 경우가 있을 경우..? 중복 발송..? logoutTime 보고 더 과거면 보내지 않기..? 확인
                System.out.println("logout = " + info.getHost() + ", cadet = " + info.getUser().getLogin());
                // 예약 있을 경우 예약 알람
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
                    // 중간에 로그아웃 한 경우 배제, 통계처리 진행할 시에 iMac이 null이 아닌 경우에 대해서 카운팅은 진행 해야함.
                    // 알림 중복처리 필요
                    iMac.updateLoginCadet(info.getUser().getLogin(), null);
                    slackService.findNotificationAndSendMessage(info.getUser().getLogin(), info.getHost(), Define.EMPTY_SEAT);
                    // 로그인 한 사람과 현재 호스트의 위치가 동일한 경우에만 알람을 보내면 중복 방지?
                    // 히스토리 기록 필요
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