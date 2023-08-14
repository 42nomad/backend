package nomad.backend.meetingroom;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
public class MeetingRoom    {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer room_id;

    @Column(name = "location", nullable = false)
    private String location;


    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "start_time")
    private Date start_time;
}
