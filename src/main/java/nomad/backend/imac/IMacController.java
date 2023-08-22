package nomad.backend.imac;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "IMacController", description = "IMac 컨트롤러")
@RestController
@RequestMapping("/cluster")
public class IMacController {


    @Operation(operationId = "getClusterDensity", summary = "각 클러스터별 밀도 조회", description = "클러스터별 밀도를 0~1 사이의 수로 계산하여 반환")
    @ApiResponse(responseCode = "200", description = "각 클러스터별 밀도 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DensityResponse.class)))
    @GetMapping("/density")
    public Map<String, Double> getDensity() {
        Map<String, Double> densities = new HashMap<String, Double>();
        densities.put("c1", 0.3);
        densities.put("c2", 0.5);
        System.out.println(densities);
        return densities;
    }

    @Operation(operationId = "getCluster", summary = "클러스터 자리 정보 조회", description = "요청 클러스터에 대해 사용중인 자리 혹은 로그아웃 후 42분내 자리들에 대한 정보 반환")
    @ApiResponse(responseCode = "200", description = "클러스터 자리 정보 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = IMacDto.class))))
    @GetMapping()
    public List<IMacDto> getClusterInfo(@Parameter(description = "클러스터 이름", required = true) @RequestParam String cluster) {
        List<IMacDto> iMacs = new ArrayList<IMacDto>();
        iMacs.add(new IMacDto(cluster, false, 34));
        return iMacs; // 자리가 있는 애들만 정보 주기. true일 떈 ㅣogoutTime -1 근데 사실 안봐서 필요없음.
    }
}
