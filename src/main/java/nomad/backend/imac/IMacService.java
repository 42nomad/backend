package nomad.backend.imac;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IMacService {
    private final IMacRepository iMacRepository;

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
        List<IMacDto> clusterInfo = new ArrayList<IMacDto>();
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
}
