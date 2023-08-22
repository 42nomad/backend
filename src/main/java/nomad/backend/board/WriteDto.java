package nomad.backend.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class WriteDto {
    @Schema(description = "분실물 습득 자리 정보")
    String location;

    @Schema(description = "게시글 내용")
    String contents;

    @Schema(description = "분실물 사진 Url")
    String imgUrl;
}
