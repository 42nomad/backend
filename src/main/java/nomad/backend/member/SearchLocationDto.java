package nomad.backend.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchLocationDto {
    @Schema(description = "즐겨찾기 위치")
    private String location;
    @Schema(description = "현재 앉아있는 사람")
    private String cadet;
    @Schema(description = "로그아웃 경과 시간")
    private Integer elapsedTime;
    @Schema(description = "즐겨찾기 여부")
    private Boolean isStarred;
}
