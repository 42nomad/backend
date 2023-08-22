package nomad.backend.meetingroom;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meetingRoom")
public class MeetingRoomController {

    @GetMapping("/{meetingRoom}")
    public MeetingRoomDto getMeetingRoomInfo(@RequestParam("meetingRoom") String meetingRoom) {
        return new MeetingRoomDto(meetingRoom, true, 34);
    }
}
