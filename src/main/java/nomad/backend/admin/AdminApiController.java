package nomad.backend.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nomad.backend.global.Define;
import nomad.backend.global.api.ApiService;
import nomad.backend.global.api.mapper.OAuthToken;
import nomad.backend.global.reponse.Response;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;
import nomad.backend.imac.IMacService;
import nomad.backend.meetingroom.MeetingRoomService;
import nomad.backend.member.Member;
import nomad.backend.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@Tag(name = "AdminController", description = "관리자 컨트롤러")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final CredentialsService credentialsService;
    private final CredentialsRepository credentialsRepository;
    private final IMacService iMacService;
    private final MeetingRoomService meetingRoomService;
    private final ApiService apiService;
    private final MemberService memberService;

    @Operation(operationId = "loginUrl", summary = "42로그인 주소 반환", description = "42로그인 중 code발급을 위한 url 반환")
    @ApiResponse(responseCode = "200", description = "주소 반환 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("/loginUrl")
    public String getLoginUrl() {
        return "https://api.intra.42.fr/oauth/authorize?client_id=u-s4t2ud-e4da46cee5b6372c0211c39eeac7b3478f15aaec565ef5c9f99e32795e6edc2b&redirect_uri=https%3A%2F%2F42nomad.kr%2Fadmin%2Fcallback&response_type=code";
    }

    @Operation(operationId = "getMemberRole", summary = "멤버 역할 반환 ", description = "Security 에 저장된 Role 을 반환")
    @ApiResponse(responseCode = "200", description = "역할 반환 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class)))
    @GetMapping("/role")
    public Integer getMemberRole(Authentication authentication) {
        Member member = memberService.getMemberByAuth(authentication);
        String role = member.getRole();
        switch (role) {
            case "ROLE_ADMIN":
                return Define.STAFF;
            case "ROLE_SUPER_ADMIN":
                return Define.ADMIN;
            default:
                return Define.USER;
        }
    }

    @Operation(operationId = "getMemberRole", summary = "멤버 역할 변경 ", description = "Security 에 저장된 Role 을 변경")
    @ApiResponse(responseCode = "200", description = "역할 변경 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class)))
    @ApiResponse(responseCode = "404", description = "해당 Intra 를 찾을 수 없습니다.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    @PostMapping("/role")
    public ResponseEntity updateMemberRole(@RequestParam String intra ,@RequestParam Integer role) {
        Member member = memberService.findByIntra(intra);
        System.out.println("updateMemberRole");
        memberService.updateMemberRole(member, role);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.ROLE_UPDATE_SUCCESS), HttpStatus.OK);
    }

    @Operation(operationId = "secret", summary = "secret DB 주입", description = "입력된 secret을 DB에 주입합니다.")
    @ApiResponse(responseCode = "200", description = "secret DB 주입 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    @PostMapping("/secret")
    public ResponseEntity insertSecret(@Parameter(description = "시크릿 아이디", required = true) @RequestBody Map<String, String> secret) {
        credentialsRepository.insertCredential(Define.SECRET_ID, secret.get(Define.SECRET_ID));
        // insert 시점으로부터 얼마 후를 슬랙봇으로 '예약'알림이 되면 담당자한테 secret 업데이트 하셈 하고 알려주기
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SECRET_INSERT_SUCCESS), HttpStatus.OK);
    }

    @Operation(operationId = "invite", summary = "슬랙 초대 주소 주입", description = "입력된 주소를 DB에 주입한다.")
    @ApiResponse(responseCode = "200", description = "슬랙 초대 주소 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    @PostMapping("/slack")
    public ResponseEntity insertSlackPath(@Parameter(description = "슬랙 주소", required = true) @RequestBody String path) {
        credentialsRepository.insertCredential(Define.SLACK_PATH, path);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SECRET_INSERT_SUCCESS), HttpStatus.OK);
    }

    @Operation(operationId = "token", summary = "token DB 주입", description = "code를 통해 발급받은 OAuthToken을 DB에 주입합니다.")
    @ApiResponse(responseCode = "200", description = "token DB 주입 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    @PostMapping("/token")
    public ResponseEntity generateAccessToken(@Parameter(description = "code", required = true) @RequestParam String code) {
        OAuthToken oAuthToken = apiService.getOAuthToken(credentialsService.getSecret(), code);
        credentialsRepository.insertCredential(Define.ACCESS_TOKEN, oAuthToken.getAccess_token());
        credentialsRepository.insertCredential(Define.REFRESH_TOKEN, oAuthToken.getRefresh_token());
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.ACCESS_TOKEN_INSERT_SUCCESS), HttpStatus.OK);
    }

    @Operation(operationId = "incluster", summary = "iMac 정보 정리", description = "서버 빌드 시 login 카뎃들만 남겨 iMac DB 정리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "iMac 정보 정리 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "42api 인증 오류로 AccessToken 발급 필요",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))})
    @PostMapping("/inCluster")
    public ResponseEntity updateAllInClusterCadet() {
        iMacService.updateAllInClusterCadet();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.IMAC_SET_SUCCESS), HttpStatus.OK);
    }

    @Operation(operationId = "deleteMember", summary = "멤버 삭제", description = "입력된 member를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "멤버 삭제 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    @ApiResponse(responseCode = "404", description = "해당 Intra 를 찾을 수 없습니다.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    @DeleteMapping("/member/{intra}")
    public ResponseEntity deleteMemberByIntra(@Parameter(description = "intra", required = true) @PathVariable String intra) {
        Member member = memberService.findByIntra(intra);
        memberService.deleteMember(member);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.MEMBER_DELETE_SUCCESS), HttpStatus.OK);
    }

    @PostMapping("/saveCluster")
    public String saveIMac() throws IOException {
        iMacService.loadCsvDataToDatabase();
        return "아이맥 저장 성공";
    }

    @PostMapping("/saveMeetingroom")
    public String saveMeetingRoom() throws IOException, ParseException {
        meetingRoomService.loadCsvDataToDatabase();
        return "회의실 저장 성공";
    }
}
