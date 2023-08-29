package nomad.backend.meetingroom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nomad.backend.global.reponse.Response;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MeetingRoomController", description = "회의실 컨트롤러")
@RestController
@RequestMapping("/meetingRoom")
@RequiredArgsConstructor
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;

    @Operation(operationId = "meetingRoom", summary = "회의실 정보 조회", description = "요청 회의실에 대한 사용 여부 및 경과 시간 정보 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회의실 정보 조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MeetingRoomDto.class)))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회의실 층",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @GetMapping()
    public List<MeetingRoomDto> getMeetingRoomInfo(@Parameter(description = "층", required = true) @RequestParam("floor") String cluster) {
        return meetingRoomService.getMeetingRoomInfoByCluster(cluster);
    }
}