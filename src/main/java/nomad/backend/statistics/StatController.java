package nomad.backend.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import nomad.backend.global.exception.InternalServerException;
import nomad.backend.global.reponse.Response;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/stat")
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Operation(operationId = "meetingRoom", summary = "회의실 통계", description = "지정된 기간에 해당하는 회의실 사용 통계를 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회의실 통계 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeetingRoomStatDto.class))),
            @ApiResponse(responseCode = "500", description = "String에서 Date형으로의 날짜 변환 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    }) // 권한 없는 사용자 접근시 403 알아서 나가는지 security에서 확인 필요, 아직 security에 stat권한은 안넣음 테스트 끝나고 나중에 보자
    @GetMapping("/meetingRoom")
    public List<MeetingRoomStatDto> getMeetingRoomStat(@RequestParam(name = "startDate") String startDate,@RequestParam(name = "endDate") String endDate,@RequestParam(name = "sort") int sort) {
        StatDto statInfo = new StatDto(startDate, endDate, sort);
        return statService.getMeetingRoomStat(parseStatInfo(statInfo));
    }

    @Operation(operationId = "meetingRoom", summary = "회의실 통계", description = "지정된 기간에 해당하는 매주 월요일 기준의 전체 아이맥 즐겨찾기 통계를 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 클러스터 즐겨찾기 통계 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = IMacStatDto.class))),
            @ApiResponse(responseCode = "500", description = "String에서 Date형으로의 날짜 변환 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/cluster/all")
    public List<IMacStatDto> getIMacStat(@RequestParam(name = "startDate") String startDate,@RequestParam(name = "endDate") String endDate,@RequestParam(name = "sort") int sort) {
        StatDto statInfo = new StatDto(startDate, endDate, sort);
        return statService.getAllStarredIMacStat(parseStatInfo(statInfo));
    }

    @Operation(operationId = "meetingRoom", summary = "회의실 통계", description = "지정된 기간에 해당하는 매주 월요일 기준의 클러스터별 아이맥 즐겨찾기 통계를 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "클러스터 즐겨찾기 통계 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = IMacStatDto.class))),
            @ApiResponse(responseCode = "500", description = "String에서 Date형으로의 날짜 변환 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/cluster")
    public List<IMacStatDto> getIMacStatByCluster(@Parameter(description = "클러스터", required = true) @RequestParam String cluster,
                                                  @RequestParam(name = "startDate") String startDate,@RequestParam(name = "endDate") String endDate,@RequestParam(name = "sort") int sort) {
        StatDto statInfo = new StatDto(startDate, endDate, sort);
        return statService.getStarredIMacStatByCluster(cluster, parseStatInfo(statInfo));
    }

    private StatDao parseStatInfo(StatDto statInfo) {
        try {
            Date startDate = dateFormat.parse(statInfo.getStartDate());
            Date endDate = dateFormat.parse(statInfo.getEndDate());
            return new StatDao(startDate, endDate, statInfo.getSort());
        } catch (Exception e) {
            throw new InternalServerException();
        }
    }

    @PostMapping("/cluster/saveTest") // saveStarredIMacStat()테스트용
    public void test() {
        statService.saveStarredIMacStat();
    }
}
