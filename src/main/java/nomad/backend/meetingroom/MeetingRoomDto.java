package nomad.backend.meetingroom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class MeetingRoomDto {
    @Schema(description = "회의실 이름")
    private String location;

    @Schema(description = "회의실 사용 여부")
    private Boolean isAvailable;

    @Schema(description = "회의실 사용 경과 시간(분)")
    private int usageTime;

    @Schema(description = "회의실 알람 여부")
    private Boolean isNoti;

    @Schema(description = "회의실 알람 아이디 정보")
    private Long notifiactionId;
}