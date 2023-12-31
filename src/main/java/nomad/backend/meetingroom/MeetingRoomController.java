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
import nomad.backend.global.reponse.StatusCode;
import nomad.backend.member.Member;
import nomad.backend.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MeetingRoomController", description = "회의실 컨트롤러")
@RestController
@RequestMapping("/meetingRoom")
@RequiredArgsConstructor
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;
    private final MemberService memberService;

    private final ReservationService reservationService;


    @Operation(operationId = "meetingRoom", summary = "회의실 정보 조회", description = "요청 회의실에 대한 사용 여부 및 경과 시간 정보 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회의실 정보 조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MeetingRoomDto.class)))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 클러스터",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @GetMapping()
    public List<MeetingRoomDto> getMeetingRoomInfo(@Parameter(description = "클러스터", required = true) @RequestParam("cluster") String cluster, Authentication authentication) {
        Member member = memberService.findByMemberId(Long.valueOf(authentication.getName()));
        return meetingRoomService.getMeetingRoomInfoByCluster(cluster.toLowerCase(), member);
    }

    @Operation(operationId = "reservation", summary = "예약공간 정보 조회", description = "요청 당일의 이노베이션아카데미 공간예약 정보를 가져온다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에약 정보 조회 성공",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservationDto.class)))),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/reservation")
    public List<ReservationDto> getReservationList() {
        return reservationService.getReservationList();
    }

    @Operation(operationId = "meetingRoomStatus", summary = "회의실 상태 변경", description = "요청 회의실에 대한 상태 변경")
    @ApiResponse(responseCode = "200", description = "회의실 상태 변경 성공")
    @PostMapping("/{cluster}/{location}")
    public ResponseEntity updateMeetingRoomStatus(@Parameter(description = "클러스터", required = true) @PathVariable String cluster, @Parameter(description = "회의실", required = true) @PathVariable String location, Authentication authentication) {
        meetingRoomService.updateMeetingRoomStatus(cluster.toLowerCase(), location);
        return new ResponseEntity(Response.res(StatusCode.OK, "MeetingRoom status update success"), HttpStatus.OK);
    }
}