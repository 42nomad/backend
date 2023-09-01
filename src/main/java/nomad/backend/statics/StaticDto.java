package nomad.backend.statics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StaticDto {
    @Schema(description = "조회 시작 시점")
    String startDate;

    @Schema(description = "조회 종료 시점")
    String endDate;

    @Schema(description = "정렬 방법")
    int sort;
}
