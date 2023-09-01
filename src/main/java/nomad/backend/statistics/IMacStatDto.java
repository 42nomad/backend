package nomad.backend.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IMacStatDto {
    @Schema(description = "아이맥")
    private String location;

    @Schema(description = "즐겨찾기 추가 횟수")
    private int count;
}
