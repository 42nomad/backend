package nomad.backend.member;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberDto {
    @Schema(description = "회원 ID")
    private Long memberId;
    @Schema(description = "인트라 ID")
    private String intra;
    @Schema(description = "refresh token")
    private String refreshToken;
    @Schema(description = "홈화면 설정 정보")
    private Integer home;
}


