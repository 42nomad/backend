package nomad.backend.imac;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class IMacDto {
     String location;
     Boolean status;
     int logOutTime;
     // status가 false일 때 logOUtTime 확인
    // 클러스터 맵에서도 사용자 정보를 보여 줄 것인지?
}
