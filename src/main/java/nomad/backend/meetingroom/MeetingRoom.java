package nomad.backend.meetingroom;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

@Entity
@Getter
public class MeetingRoom    {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer room_id;

    @Column(nullable = false)
    private int floor;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Boolean status;

    @Column(name = "start_time")
    private Date startTime;
}
