package nomad.backend.imac;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nomad.backend.global.api.ApiService;
import nomad.backend.global.api.mapper.OAuthToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Tag(name = "IMacController", description = "IMac 컨트롤러")
@RestController
@RequestMapping("/cluster")
@RequiredArgsConstructor
public class IMacController {

    private final IMacService iMacService;
    private final ApiService apiService;

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

    //To Do: 관리자 메소드로 옮기거나 프로젝트 첫 실행 시만 실행될 수 있는 방법 찾기
    @PostMapping()
    public void saveIMac() throws IOException {
        iMacService.loadCsvDataToDatabase();
    }

    @GetMapping("/auth/callback")
    public String returnCode(@RequestParam String code) {
        System.out.println("code = " + code);
        return code;
    }
    @PostMapping("/auth/token")
    public String getToken(HttpServletRequest req, @RequestParam String code) {
        OAuthToken oAuthToken = apiService.getOAuthToken(code);
        System.out.println("token = " + oAuthToken.getAccess_token());
        return oAuthToken.getAccess_token();
    }

    @PostMapping("/allTest")
    public String test(@RequestParam String token) {
        iMacService.updateAllInClusterCadet(token);
        return "전체 로그인 업데이트 끝";
    }

    @PostMapping("/inout")
    public String test2(@RequestParam String token) {
        iMacService.update3minClusterInfo(token);
        return "inout 업데이트 끝";
    }
}
