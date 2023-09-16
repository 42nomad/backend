package nomad.backend.imac;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nomad.backend.admin.CredentialsService;
import nomad.backend.global.Define;
import nomad.backend.global.api.ApiService;
import nomad.backend.global.api.mapper.Cluster;
import nomad.backend.global.exception.NotFoundException;
import nomad.backend.history.HistoryService;
import nomad.backend.slack.SlackService;
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
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
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
        if (iMacList.isEmpty())
            throw new NotFoundException();
        return parseIMacList(iMacList);
    }

    public IMacDto parseIMac(IMac iMac) {
        return toIMacDto(iMac);
    }

    public List<IMacDto> parseIMacList(List<IMac> iMacList) {
        return iMacList.stream()
                .map(this::toIMacDto)
                .collect(Collectors.toList());
    }

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
    @PostConstruct
    public void updateAllInClusterCadet() {
        int page = 1;
        Date now = new Date();
        System.out.println("incluster 시작");
        while (true) {
            List<Cluster> clusterCadets = apiService.getAllLoginCadets(credentialsService.getAccessToken(), page);
            for (Cluster info : clusterCadets) {
                System.out.println("incluster = " + info.getUser().getLocation() + ", " + info.getHost());
                String location = info.getUser().getLocation();
                if (location != null)
                    historyService.addHistory(location, info.getUser().getLogin(), info.getBegin_at());
                if (!info.getHost().equalsIgnoreCase(location))
                    continue;
                IMac iMac = iMacRepository.findByLocation(location);
                if (iMac == null)
                    continue;
                if (info.getUser().getLogin().equalsIgnoreCase(iMac.getCadet())) {
                    iMac.updateLoginCadet(now);
                    System.out.println("(시간만 업데이트) user = " +  info.getUser().getLogin() + " 자리 = " + info.getHost());
                }
                else {
                    Instant instant = Instant.parse(info.getBegin_at());
                    iMac.updateLoginCadet(info.getUser().getLogin(), now, new Date(instant.toEpochMilli()));
                    slackService.findIMacNotificationAndSendMessage(info.getUser().getLogin(), location,
                            Define.TAKEN_SEAT + "(서버 재시작으로 인하여 실제 자리 사용 시작 시간보다 알람이 지연 발송됐을 수 있습니다. 양해 부탁드립니다.)");
                    System.out.println("(전부 업데이트) user = " +  info.getUser().getLogin() + " 자리 = " + info.getHost());
                }
            }
            if (clusterCadets.get(99).getEnd_at() != null)
                break;
            page++;
        }
        List<IMac> needToLogoutIMacs = iMacRepository.findByCadetAndUpdatedAt(now);
        needToLogoutIMacs.forEach(imac -> {
                    imac.forceLogout();
                    slackService.findIMacNotificationAndSendMessage(null, imac.getLocation(),
                             Define.EMPTY_SEAT + "(서버 재시작으로 인하여 본인이 로그아웃 한 자리 또는 중복 알림일 수 있습니다. 양해 부탁드립니다.)");});
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
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
                historyService.addHistory(info.getHost(), info.getUser().getLogin(), info.getBegin_at());
                IMac iMac = iMacRepository.findByLocation(info.getHost());
                if (iMac != null && info.getHost().equalsIgnoreCase(info.getUser().getLocation())) {
                    Date loginTime = new Date(Instant.parse(info.getBegin_at()).toEpochMilli());
                    if (iMac.getLoginTime() != null && !iMac.getLoginTime().before(loginTime))
                        continue;
                    iMac.updateLoginCadet(info.getUser().getLogin(), null, loginTime);
                    slackService.findIMacNotificationAndSendMessage(info.getUser().getLogin(), info.getHost(),  Define.TAKEN_SEAT);
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
        try (BufferedReader br = new BufferedReader(new FileReader("/home/ec2-user/apps/backend/src/main/java/nomad/backend/imac/imac.csv", 정Charset.forName("UTF-8")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                IMac iMac = new IMac(data[0], data[1]);
                iMacRepository.save(iMac);
            }
        }
    }
}