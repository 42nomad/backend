package nomad.backend.meetingroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class MeetingRoomDto {
    String meetingRoom;
    boolean status;
    int usageTime;
    // usageTime int로 ex) 80(분), / String - 1시간 20분
}
