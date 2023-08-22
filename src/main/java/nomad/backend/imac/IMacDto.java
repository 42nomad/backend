package nomad.backend.imac;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class IMacDto {
     @Schema(description = "imac 자리 이름")
     String location;

     @Schema(description = "자리 점유 상태")
     Boolean status;

     @Schema(description = "로그아웃 후 42분 내 경과 시간(점유 중이거나, 비어있는 자리일 경우 -1)")
     int logOutTime;
}
