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
    private String cluster;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Boolean status;

    @Column
    private Date startTime;

    public MeetingRoom(String cluster, String location) {
        this.cluster = cluster;
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
