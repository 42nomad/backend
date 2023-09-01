package nomad.backend.statics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import nomad.backend.global.exception.custom.InternalServerException;
import nomad.backend.global.reponse.Response;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/static")
@RequiredArgsConstructor
public class StaticController {
    private final StaticsService staticsService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Operation(operationId = "meetingRoom", summary = "회의실 통계", description = "지정된 기간에 해당하는 회의실 사용 통계를 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회의실 통계 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeetingRoomStaticDto.class))),
            @ApiResponse(responseCode = "500", description = "String에서 Date형으로의 날짜 변환 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    }) // 권한 없는 사용자 접근시 403 알아서 나가는지 security에서 확인 필요
    @GetMapping("/meetingRoom")
    public List<MeetingRoomStaticDto> getMeetingRoomStatic(@Parameter(description = "조회 정보", required = true) @RequestBody StaticDto staticInfo) {
        try {
            Date startDate = dateFormat.parse(staticInfo.getStartDate());
            Date endDate = dateFormat.parse(staticInfo.getEndDate());
            return staticsService.getMeetingRoomStatics(startDate, endDate, staticInfo.getSort());
        } catch (Exception e) {
            throw new InternalServerException();
        }
    }

    @GetMapping("/cluster")
    public List<IMacStaticDto> getIMacStatic(@Parameter(description = "조회 정보", required = true) @RequestBody StaticDto staticInfo) {
        return new ArrayList<>();
    }

    @GetMapping("/cluster")
    public List<IMacStaticDto> getIMacStaticByCluster(@Parameter(description = "클러스터", required = true) @RequestParam String cluster,
                                                      @Parameter(description = "조회 정보", required = true) @RequestBody StaticDto staticInfo) {
        return new ArrayList<>();
    }
}
