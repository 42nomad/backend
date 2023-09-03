package nomad.backend.statistics;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staticId;

    @Column(nullable = false)
    private int type;

    @Column(nullable = false)
    private String cluster;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Date usedDate;

    @Column(nullable = false)
    private int data;

    public Stat(int type, String cluster, String location, Date usedDate, int data) {
        this.type = type;
        this.cluster = cluster;
        this.location = location;
        this.usedDate = usedDate;
        this.data = data;
    }
}
