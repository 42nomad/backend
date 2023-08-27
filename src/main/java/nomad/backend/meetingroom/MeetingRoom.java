package nomad.backend.meetingroom;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingRoom    {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;

    @Column(nullable = false)
    private int floor;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Boolean status;

    @Column(name = "start_time")
    private Date startTime;

    public MeetingRoom(int floor, String location) {
        this.floor = floor;
        this.location = location;
        this.status = false;
    }

    public void updateStatus(Date startTime) {
        this.status = true;
        this.startTime = startTime;
    }

    public void updateStatus() {
        this.status = false;
    }
}
