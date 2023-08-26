package nomad.backend.imac;

import lombok.RequiredArgsConstructor;
import nomad.backend.global.api.ApiService;
import nomad.backend.global.api.mapper.Cluster;
import nomad.backend.member.MemberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
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
    private final MemberRepository memberRepository;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

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
        boolean isUsed = iMac.getCadet() != null;
        if (isUsed || elapsedTime < 43)
            return new IMacDto(iMac.getLocation(), iMac.getCadet(), isUsed, elapsedTime);
        else {
            iMac.resetLogoutTime();
            return new IMacDto(iMac.getLocation(), iMac.getCadet(), false, -1);
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
    public void loadCsvDataToDatabase() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("./src/main/java/nomad/backend/imac/imac.csv", Charset.forName("UTF-8")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                IMac iMac = new IMac(data[0], data[1]);
                iMacRepository.save(iMac);
            }
        }
    }

    //To do : 백그라운드 돌리는 토큰 관리 필요. 테이블 어디에? admin 테이블로?
    // 서버 재업시에 돌릴 수 있도록 관리자 메뉴 필요
    @Transactional
    public void updateAllInClusterCadet(String token) {
        int page = 1;
//        token42 = adminRepository.callAdmin();
        while (true) {
            List<Cluster> clusterCadets = apiService.getAllLoginCadets(token, page);
            for (Cluster info : clusterCadets) {
                String location = info.getUser().getLocation();
                if (!info.getHost().equalsIgnoreCase(location))
                    break;
                // 현재 그 자리를 사용하는 경우에만 업데이트, logout한 자리에 대해서는 기록하지 않음.
                // 서버가 꺼져있는 동안 로그아웃 한 자리에 대해서 어떻게 처리를 하지?
                // 지금 정리되는 자리 외에 전부 null 처리?? 그게 가능한가??
                // 정보 수정 컬럼을 두고 그 시간으로 비교..? 그 시간이 아닌 애들을 전부 cadet에 null처리? LogoutTime은?
                IMac iMac = iMacRepository.findByLocation(location);
                if (iMac == null) // 가끔 다른 나라 정보가 섞여들어옴
                    continue;
                iMac.updateLoginCadet(info.getUser().getLogin());
                // cadet에 넣어주고, LogoutTime을 null로 바꿈
                // 사용하고 있는 유저가 멤버면 history에 기록을 해줘야 하는데
                // 이 메소드의 경우 서버가 꺼졌다 켜질때만 되는 거임.
                // history에서 당일 같은 자리에 대한 중복검증?
                // 슬랫봇 할 경우 이 자리에 예약건 사람한테 알림
            }
            if (clusterCadets.get(99).getEnd_at() != null)
                break;
            page++;
        }
    }

    //백그라운드 3분마다 돌 것인지?? 1분..?
//        @Scheduled(cron = "0 0/2 * 1/1 * ?")
    @Transactional
    public void update3minClusterInfo(String token){
        int page = 1;
        //        token42 = adminRepository.callAdmin();
        while (true) {
            List<Cluster> logoutCadets = apiService.getRecentlyLogoutCadet(token, page);
            for (Cluster info : logoutCadets) {
                IMac iMac = iMacRepository.findByLocation(info.getHost());
                if (iMac == null)
                    continue;
                Instant instant = Instant.parse(info.getEnd_at());
                iMac.updateLogoutCadet(new Date(instant.toEpochMilli()), info.getUser().getLogin());
                // cadet null로 바꾸고, logoutTime이 null이거나 들어가있는 것보다 최근일 경우에만 LogoutTime과 leftCadet을 갱신해줌
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
            List<Cluster> loginCadets = apiService.getRecentlyLoginCadet(token, page);
            for (Cluster info : loginCadets) {
                IMac iMac = iMacRepository.findByLocation(info.getHost());
                if (iMac != null && info.getHost().equalsIgnoreCase(info.getUser().getLogin())) {
                    // 중간에 로그아웃 한 경우 배제, 통계처리 진행할 시에 iMac이 null이 아닌 경우에 대해서 카운팅은 진행 해야함.
                    iMac.updateLoginCadet(info.getUser().getLogin());
                    // 히스토리 기록 필요
                    System.out.println("login = " + info.getHost() + ", intra = " + info.getUser().getLogin());
                }
            }
            if (loginCadets.size() < 50)
                break;
            page++;
        }
    }
}