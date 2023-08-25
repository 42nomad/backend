package nomad.backend.imac;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import nomad.backend.global.api.ApiService;
import nomad.backend.global.api.mapper.Cluster;
import nomad.backend.member.Member;
import nomad.backend.member.MemberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IMacService {
    private final IMacRepository iMacRepository;
    private final ApiService apiService;
    private final MemberRepository memberRepository;

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
        List<IMacDto> clusterInfo = new ArrayList<>();
        iMacList.forEach(iMac -> {
            int elapsedTime = getElapsedTime(iMac.getCadet(), iMac.getLogoutTime());
            if (iMac.getCadet() == null && elapsedTime > 42)
                iMac.resetLogoutTime();
            else
                clusterInfo.add(new IMacDto(iMac.getLocation(), false, elapsedTime));
        });
        return clusterInfo;
    }

    private int getElapsedTime(String isEmpty, Date logoutTime) {
        if (isEmpty != null)
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
    @Transactional
    public void updateAllInClusterCadet(String token) {
        int page = 1;
//        token42 = adminRepository.callAdmin();
//        while (true) {
            List<Cluster> clusterCadets = apiService.getLoginCadets(token, page);
            for (Cluster info : clusterCadets) {
                String location = info.getUser().getLocation();
                if (info.getHost().equalsIgnoreCase(location)) {
                    IMac iMac = iMacRepository.findByLocation(location);
                    if (iMac == null)
                        continue;
                    iMac.updateLoginCadet(info.getUser().getLogin());
                }
                // 사용하고 있는 유저가 멤버면 history에 기록을 해줘야 하는데
                // 이 메소드의 경우 서버가 꺼졌다 켜질때만 되는 거임.
                // history에서 당일 같은 자리에 대한 중복검증을 어떻게 진행해야하지?
            }
//            if (clusterCadets.get(9).getEnd_at() != null)
//                break;
//            page++;
//        }
    }


}
