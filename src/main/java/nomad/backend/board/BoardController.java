package nomad.backend.board;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/board")
public class BoardController {


    @GetMapping()
    List<BoardDto> getBoardList() {
        List<BoardDto> boardList = new ArrayList<BoardDto>();
        boardList.add(new BoardDto(Long.valueOf(1), "c1s2r3", "imgurl", "date"));
        return boardList;
    }

    @GetMapping("/{postId}")
    PostDto getPostInfo(@PathVariable("postId") Long postId) {
        // date yyyy-mm-dddd
        return new PostDto(postId, "hyunjcho", "c1s2r3", "contents", "imgurl", "date", true);
    }

    @PostMapping()
    int writePost(@RequestBody WriteDto post) {
        // save content 작성자 찾아내서
        // 기본 image는 default로 온다
        return 200; // response 형식으로 바꾸기
    }

    @PatchMapping("/{postId}")
    int modifyPost(@PathVariable() Long postId, @RequestBody WriteDto modifyPost) {
        // 작성날짜 수정 안하고, 이미지 - 콘텐츠 - 좌석 전부 수정 가능
        return 200; // response 형식으로 바꾸기
    }

    @DeleteMapping("/{postId}")
    int deletePost(@PathVariable() Long postId) {
        return 200; // reponse 형식으로 바꾸기
    }

}
