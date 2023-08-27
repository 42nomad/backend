package nomad.backend.imac;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class DensityInfo {
    @Schema(description = "클러스터별 밀도 정보")
    private Map<String, Double> clusters = new HashMap<>();

    public DensityInfo() {
        clusters.put("c1", 63D);
        clusters.put("c2", 80D);
        clusters.put("c3", 63D);
        clusters.put("c4", 80D);
        clusters.put("c5", 63D);
        clusters.put("c6", 80D);
        clusters.put("cx1", 28D);
        clusters.put("cx2", 56D);
    }
}
