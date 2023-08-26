package nomad.backend.history;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HistoryDto {
    @Schema(description = "자리 번호")
    private String locaiton;
    @Schema(description = "앉아있는 사람 Intra")
    private String cadet;
    @Schema(description = "로그아웃 경과 시간")
    private Integer elapsedTime;
    @Schema(description = "로그인 시간")
    private String lastLogin;
}
