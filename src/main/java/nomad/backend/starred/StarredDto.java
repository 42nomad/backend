package nomad.backend.starred;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nomad.backend.imac.IMac;
import nomad.backend.member.Member;

@Getter @Setter
@AllArgsConstructor
public class StarredDto {
    @Schema(description = "즐겨찾기 번호")
    private Integer starredId;
    @Schema(description = "본인 인트라")
    private String owner;
    @Schema(description = "즐겨찾기 위치")
    private String location;
}
