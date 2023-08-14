package nomad.backend.imac;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
public class IMac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imac_id;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "cadet")
    private String cadet;

    @Column(name = "logout_time")
    private Date logout_time;

    @Column(name = "left_cadet")
    private Date left_cadet;
}
