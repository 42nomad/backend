package nomad.backend.imac;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "IMacController", description = "IMac 컨트롤러")
@RestController
@RequestMapping("/cluster")
@RequiredArgsConstructor
public class IMacController {

    private final IMacService iMacService;

    @Operation(operationId = "getClusterDensity", summary = "각 클러스터별 밀도 조회", description = "클러스터별 밀도를 0~1 사이의 수로 계산하여 반환")
    @ApiResponse(responseCode = "200", description = "각 클러스터별 밀도 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DensityInfo.class)))
    @GetMapping("/density")
    public Map<String, Double> getDensity() {
        List<String> clusters = Arrays.asList("c1", "c2", "c3", "c4", "c5", "c6", "cx1", "cx2");
        Map<String, Double> densities = new HashMap<>();
        for (String cluster : clusters)
            densities.put(cluster, iMacService.getClusterDensity(cluster));
        return densities;
    }

    @Operation(operationId = "getCluster", summary = "클러스터 자리 정보 조회", description = "요청 클러스터에 대해 사용중인 자리 혹은 로그아웃 후 42분내 자리들에 대한 정보 반환")
    @ApiResponse(responseCode = "200", description = "클러스터 자리 정보 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = IMacDto.class))))
    @GetMapping()
    public List<IMacDto> getClusterInfo(@Parameter(description = "클러스터 이름", required = true) @RequestParam String cluster) {
        return iMacService.getClusterInfo(cluster);
    }
}
