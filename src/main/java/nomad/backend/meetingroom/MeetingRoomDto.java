package nomad.backend.meetingroom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class MeetingRoomDto {
    @Schema(description = "회의실 이름")
    String meetingRoom;

    @Schema(description = "회의실 사용 여부")
    boolean status;

    @Schema(description = "회의실 사용 경과 시간(분)")
    int usageTime;
    // usageTime int로 ex) 80(분), / String - 1시간 20분
}
