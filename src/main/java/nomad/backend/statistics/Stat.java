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
    // meetingRoom은 누적사용시간을 분으로 저장하고, imac은 조회 시점의 갯수를 저장

    public Stat(int type, String cluster, String location, Date usedDate, int data) {
        this.type = type;
        this.cluster = cluster;
        this.location = location;
        this.usedDate = usedDate;
        this.data = data;
    }
}
