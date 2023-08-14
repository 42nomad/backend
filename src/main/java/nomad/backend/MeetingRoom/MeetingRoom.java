package nomad.backend.MeetingRoom;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "imac")
@Getter
@Setter
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
