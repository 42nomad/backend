package nomad.backend.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import nomad.backend.global.api.ApiService;
import nomad.backend.global.api.mapper.OAuthToken;
import nomad.backend.global.reponse.Response;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;
import nomad.backend.imac.IMacService;
import nomad.backend.meetingroom.MeetingRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final CredentialsService credentialsService;
    private final CredentialsRepository credentialsRepository;
    private final IMacService iMacService;
    private final MeetingRoomService meetingRoomService;
    private final ApiService apiService;

    @Operation(operationId = "loginUrl", summary = "42로그인 주소 반환", description = "42로그인 중 code발급을 위한 url 반환")
    @ApiResponse(responseCode = "200", description = "주소 반환 성공")
    @GetMapping("/loginUrl")
    public String getLoginUrl() {
        return "https://api.intra.42.fr/oauth/authorize?client_id=u-s4t2ud-9e9d9a8349093bbe40ba6f4dcaafa2b4905a0eff3eaa2a380f94b9ebc30c0dd9&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fadmin%2Fcallback&response_type=code";
    }

    // 모든 메서드에 권한 확인 코드 추가 필요
    //secret, accessToken 이런거 define으로 하는게 좋을지?
    @Operation(operationId = "secret", summary = "secret DB 주입", description = "입력된 secret을 DB에 주입합니다.")
    @ApiResponse(responseCode = "200", description = "secret DB 주입 성공")
    @PostMapping("/secret")
    public ResponseEntity insertSecret(@RequestBody Map<String, String> secret) {
        credentialsRepository.insertCredential("secret", secret.get("secret"));
        // insert 시점으로부터 얼마 후를 슬랙봇으로 '예약'알림이 되면 담당자한테 secret 업데이트 하셈 하고 알려주기
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SECRET_INSERT_SUCCESS), HttpStatus.OK);
    }

    @Operation(operationId = "token", summary = "token DB 주입", description = "code를 통해 발급받은 OAuthToken을 DB에 주입합니다.")
    @ApiResponse(responseCode = "200", description = "token DB 주입 성공")
    @PostMapping("/token")
    public ResponseEntity generateAccessToken(@RequestParam String code) {
        OAuthToken oAuthToken = apiService.getOAuthToken(credentialsService.getSecret(), code);
        credentialsRepository.insertCredential("accessToken", oAuthToken.getAccess_token());
        credentialsRepository.insertCredential("refreshToken", oAuthToken.getRefresh_token());
        return new ResponseEntity(Response.res(StatusCode.OK, oAuthToken.getAccess_token()), HttpStatus.OK);
    } // 나중에 응답 메시지 "access Token 발급 성공"으로 변경

    @Operation(operationId = "incluster", summary = "iMac 정보 정리", description = "서버 빌드 시 login 카뎃들만 남겨 iMac DB 정리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "iMac 정보 정리 성공"),
            @ApiResponse(responseCode = "401", description = "42api 인증 오류로 AccessToken 발급 필요")
    })
    @PostMapping("/inCluster")
    public ResponseEntity test() { // 401 나갈 수 있음. 관리자 페이지로 돌아가서 어세스토큰 발급을 시켜줘야 함.
        iMacService.updateAllInClusterCadet();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.IMAC_SET_SUCCESS), HttpStatus.OK);
    }

    // 서버 최초 빌드 시 1회 사용
    @PostMapping("/saveCluster")
    public String saveIMac() throws IOException {
        iMacService.loadCsvDataToDatabase();
        return "아이맥 저장 성공";
    }

    // 서버 최초 빌드 시 1회 사용
    @PostMapping("/saveMeetingroom")
    public String saveMeetingRoom() throws IOException, ParseException {
        meetingRoomService.loadCsvDataToDatabase();
        return "회의실 저장 성공";
    }

    // 백그라운드 잘 돌아가면 없애야 하는 메소드
    @PostMapping("/inout")
    public String test2() {
        iMacService.update3minClusterInfo();
        return "inout 업데이트 끝";
    }

    // 이거는 프론트가 하게 되면 없어져도 되는 메소드
    @GetMapping("/callback")
    public String returnCode(@RequestParam String code) {
        return code;
    }
}
