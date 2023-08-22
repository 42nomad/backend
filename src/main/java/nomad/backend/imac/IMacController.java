package nomad.backend.imac;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cluster")
public class IMacController {


    @GetMapping("/density")
    public Map<String, Double> getDensity() {
        Map<String, Double> densities = new HashMap<String, Double>();
        densities.put("c1", 0.3);
        densities.put("c2", 0.5);
        System.out.println(densities);
        return densities;
    }

    @GetMapping("/{cluster}")
    public List<IMacDto> getClusterInfo(@PathVariable("cluster") String cluster) {
        List<IMacDto> iMacs = new ArrayList<IMacDto>();
        iMacs.add(new IMacDto(cluster, false, 34));
        return iMacs; // 자리가 있는 애들만 정보 주기. true일 떈 ㅣogoutTime -1 근데 사실 안봐서 필요없음.
    }
}
