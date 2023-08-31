package nomad.backend.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class BoardDto {
    @Schema(description = "게시글 번호")
    Long postId;

    @Schema(description = "분실물 습득 자리 정보")
    String location;

    @Schema(description = "분실물 사진 Key")
    String imgKey;

    @Schema(description = "작성 날짜(yyyy-mm-dd)")
    String date;
}