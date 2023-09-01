package nomad.backend.statics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MeetingRoomStaticDto {
    @Schema(description = "클러스터")
    private String cluster;

    @Schema(description = "회의실")
    private String location;

    @Schema(description = "사용 횟수")
    private int count;

    @Schema(description = "누적 사용 시간")
    private int totalUsageTime;
}
