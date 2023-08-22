package nomad.backend.meetingroom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "MeetingRoomController", description = "회의실 컨트롤러")
@RestController
@RequestMapping("/meetingRoom")
public class MeetingRoomController {
    @Operation(operationId = "meetingRoom", summary = "회의실 정보 조회", description = "요청 회의실에 대한 사용 여부 및 경과 시간 정보 반환")
    @ApiResponse(responseCode = "200", description = "회의실 정보 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MeetingRoomDto.class))))
    @GetMapping()
    public List<MeetingRoomDto> getMeetingRoomInfo(@Parameter(description = "층", required = true) @RequestParam("floor") int floor) {
        List<MeetingRoomDto> meetingRooms = new ArrayList<MeetingRoomDto>();
        // 각 층에 포함되는 미팅룸 정보 전체 조회
        meetingRooms.add(new MeetingRoomDto("location", true, 34));
        return meetingRooms;
    }
}
