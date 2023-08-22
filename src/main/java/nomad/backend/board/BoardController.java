package nomad.backend.board;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "BoardController", description = "게시판 컨트롤러")
@RestController
@RequestMapping("/board")
public class BoardController {


    @Operation(operationId = "boardList", summary = "게시판 전체 조회", description = "게시판에 작성된 모든 글 정보 반환")
    @ApiResponse(responseCode = "200", description = "게시판 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BoardDto.class)))
    )
    @GetMapping("/list")
    List<BoardDto> getBoardList() {
        List<BoardDto> boardList = new ArrayList<BoardDto>();
        boardList.add(new BoardDto(Long.valueOf(1), "c1s2r3", "imgurl", "date"));
        return boardList;
    }

    @Operation(operationId = "getPostInfo", summary = "게시물 상세 정보", description = "한 게시물에 대한 상세 정보 반환")
    @ApiResponse(responseCode = "200", description = "게시물 상세 정보 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))
    )
    @GetMapping()
    PostDto getPostInfo(@Parameter(description = "게시글 번호", required = true) @RequestParam("postId") Long postId) {
        // date yyyy-mm-dddd
        return new PostDto(postId, "hyunjcho", "c1s2r3", "contents", "imgurl", "date", true);
    }

    @Operation(operationId = "post", summary = "게시물 등록", description = "게시물 DB 등록")
    @ApiResponse(responseCode = "200", description = "게시물 등록 성공")
    @PostMapping()
    int writePost(@Parameter(description = "게시물 작성 필요 정보", required = true) @RequestBody WriteDto post) {
        // save content 작성자 찾아내서
        // 기본 image는 default로 온다
        return 200; // response 형식으로 바꾸기
    }

    @Operation(operationId = "modify", summary = "게시물 수정", description = "게시물 DB 수정")
    @ApiResponse(responseCode = "200", description = "게시물 수정 성공")
    @PatchMapping("/{postId}")
    int modifyPost(@Parameter(description = "게시물 번호", required = true) @PathVariable("postId") Long postId, @Parameter(description = "수정할 게시글 내용", required = true) @RequestBody WriteDto modifyPost) {
        // 작성날짜 수정 안하고, 이미지 - 콘텐츠 - 좌석 전부 수정 가능
        return 200; // response 형식으로 바꾸기
    }

    @Operation(operationId = "deletePost", summary = "게시물 삭제", description = "게시물 DB 삭제")
    @ApiResponse(responseCode = "200", description = "게시물 삭제 성공")
    @DeleteMapping("/{postId}")
    int deletePost(@Parameter(description = "게시물 번호", required = true) @PathVariable("postId") Long postId) {
        return 200; // reponse 형식으로 바꾸기
    }
}
