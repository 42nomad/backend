package nomad.backend.board;

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
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;
import nomad.backend.member.Member;
import nomad.backend.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "BoardController", description = "게시판 컨트롤러")
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;

    @Operation(operationId = "boardList", summary = "게시판 전체 조회", description = "게시판에 작성된 모든 글 정보 반환")
    @ApiResponse(responseCode = "200", description = "게시판 조회 성공",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BoardDto.class))))
    @GetMapping("/list")
    public List<BoardDto> getBoardList() {
        return boardService.getAllPost();
    }

    @Operation(operationId = "post", summary = "게시물 등록", description = "게시물 DB 등록")
    @ApiResponse(responseCode = "200", description = "게시물 등록 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    @PostMapping()
    public ResponseEntity writePost(@Parameter(description = "게시물 작성 필요 정보", required = true) @RequestBody WriteDto post, Authentication authentication) {
        Member member = memberService.findByMemberId(Long.valueOf(authentication.getName()));
        boardService.writePost(member, post);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.POST_WRITE_SUCCESS), HttpStatus.OK);
    }

    @Operation(operationId = "modify", summary = "게시물 수정", description = "게시물 DB 수정")
    @ApiResponse(responseCode = "200", description = "게시물 수정 성공")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 수정 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @PatchMapping("/{postId}")
    public ResponseEntity modifyPost(@Parameter(description = "게시물 번호", required = true) @PathVariable("postId") Long postId, @Parameter(description = "수정할 게시글 내용", required = true) @RequestBody WriteDto modifyPost) {
        boardService.modifyPost(postId, modifyPost);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.POST_MODIFY_SUCCESS), HttpStatus.OK);
    }

    @Operation(operationId = "getPostInfo", summary = "게시물 상세 정보", description = "한 게시물에 대한 상세 정보 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 상세 정보 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @GetMapping()
    public PostDto getPostInfo(@Parameter(description = "게시글 번호", required = true) @RequestParam("postId") Long postId, Authentication authentication) throws NullPointerException{
        Long memberId = Long.valueOf(authentication.getName());
        return boardService.getPostInfo(memberId, postId);
    }

    @Operation(operationId = "deletePost", summary = "게시물 삭제", description = "게시물 DB 삭제")
    @ApiResponse(responseCode = "200", description = "게시물 삭제 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    @DeleteMapping("/{postId}")
    public ResponseEntity deletePost(@Parameter(description = "게시물 번호", required = true) @PathVariable("postId") Long postId) {
        boardService.deletePostByPostId(postId);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.POST_DELETE_SUCCESS), HttpStatus.OK);
    }
}
