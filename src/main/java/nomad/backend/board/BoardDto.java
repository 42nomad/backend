package nomad.backend.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class BoardDto {
    Long postId;
    String location;
    String imgUrl;
    String date;
}