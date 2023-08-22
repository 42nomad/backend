package nomad.backend.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class WriteDto {
    String location;
    String contents;
    String imgUrl;
}
