package nomad.backend.meetingroom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationDto {
    @Schema(description = "예약 공간 이름")
    private String revTitle;
    @Schema(description = "예약 공간 장소")
    private String revLocation;
    @Schema(description = "예약된 시간")
    private String revTime;

    @Override
    public String toString() {
        return "ReservationDto{" +
                "revTitle='" + revTitle + '\'' +
                ", revLocation='" + revLocation + '\'' +
                ", revTime='" + revTime + '\'' +
                '}';
    }
}

