package nomad.backend.starred;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nomad.backend.imac.IMac;
import nomad.backend.imac.IMacDto;
import nomad.backend.member.Member;


@Getter
@Setter
@AllArgsConstructor
public class StarredDto {
    @Schema(description = "즐겨찾기 번호")
    private Integer starredId;
    @Schema(description = "즐겨찾기 위치")
    private String location;
    @Schema(description = "현재 앉아있는 사람")
    private String cadet;
    @Schema(description = "로그아웃 경과 시간")
    private Integer elapsedTime;
    @Schema(description = "현재 사용가능 여부")
    private Boolean isAvailable;
    @Schema(description = "알람 설정 여부")
    private Boolean IsNoti;
    @Schema(description = "알람 설정 아이디 정보")
    private Long notificationId;
}
