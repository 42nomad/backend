package nomad.backend.statics;

import io.swagger.v3.oas.annotations.media.Schema;

public class IMacStaticDto {
    @Schema(description = "클러스터")
    private String cluster;

    @Schema(description = "아이맥")
    private String location;

    @Schema(description = "즐겨찾기 추가 횟수")
    private int count;
}
