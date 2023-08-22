package nomad.backend.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class PostDto {
    Long postId;
    String writer;
    String location;
    String contents;
    String imgUrl;
    String date; // 이것
    boolean isMine;
}
