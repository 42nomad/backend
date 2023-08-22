package nomad.backend.imac;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class DensityResponse {
    @Schema(description = "클러스터별 밀도 정보")
    private Map<String, Double> density;
}
