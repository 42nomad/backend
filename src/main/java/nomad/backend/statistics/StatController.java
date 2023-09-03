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
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Operation(operationId = "meetingRoom", summary = "회의실 통계", description = "지정된 기간에 해당하는 회의실 사용 통계를 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회의실 통계 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeetingRoomStatDto.class))),
            @ApiResponse(responseCode = "500", description = "String에서 Date형으로의 날짜 변환 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))})
    @GetMapping("/meetingRoom")
    public List<MeetingRoomStatDto> getMeetingRoomStat(@Parameter(description = "조회 시작일", required = true) @RequestParam(name = "startDate") String startDate,
                                                       @Parameter(description = "조회 종료일", required = true) @RequestParam(name = "endDate") String endDate,
                                                       @Parameter(description = "정렬 방식", required = true) @RequestParam(name = "sort") int sort) {
        return statService.getMeetingRoomStat(parseStatInfo(startDate, endDate, sort));
    }

    @Operation(operationId = "meetingRoom", summary = "회의실 통계", description = "지정된 기간에 해당하는 매주 월요일 기준의 전체 아이맥 즐겨찾기 통계를 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 클러스터 즐겨찾기 통계 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = IMacStatDto.class))),
            @ApiResponse(responseCode = "500", description = "String에서 Date형으로의 날짜 변환 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))})
    @GetMapping("/cluster/all")
    public List<IMacStatDto> getIMacStat(@Parameter(description = "조회 시작일", required = true) @RequestParam(name = "startDate") String startDate,
                                         @Parameter(description = "조회 종료일", required = true) @RequestParam(name = "endDate") String endDate,
                                         @Parameter(description = "정렬 방식", required = true) @RequestParam(name = "sort") int sort) {
        return statService.getAllStarredIMacStat(parseStatInfo(startDate, endDate, sort));
    }

    @Operation(operationId = "meetingRoom", summary = "회의실 통계", description = "지정된 기간에 해당하는 매주 월요일 기준의 클러스터별 아이맥 즐겨찾기 통계를 제공합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "클러스터 즐겨찾기 통계 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = IMacStatDto.class))),
            @ApiResponse(responseCode = "500", description = "String에서 Date형으로의 날짜 변환 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))})
    @GetMapping("/cluster")
    public List<IMacStatDto> getIMacStatByCluster(@Parameter(description = "클러스터", required = true) @RequestParam(name = "cluster") String cluster,
                                                  @Parameter(description = "조회 시작일", required = true) @RequestParam(name = "startDate") String startDate,
                                                  @Parameter(description = "조회 종료일", required = true) @RequestParam(name = "endDate") String endDate,
                                                  @Parameter(description = "정렬 방식", required = true) @RequestParam(name = "sort") int sort) {
        return statService.getStarredIMacStatByCluster(cluster, parseStatInfo(startDate, endDate, sort));
    }

    private StatDao parseStatInfo(String start, String end, int sort) {
        try {
            Date startDate = dateFormat.parse(start + " 00:00:00");
            Date endDate = dateFormat.parse(end + " 23:59:59");
            return new StatDao(startDate, endDate, sort);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    @PostMapping("/cluster/saveTest") // saveStarredIMacStat()테스트용
    public void test() {
        statService.saveStarredIMacStat();
    }
}
