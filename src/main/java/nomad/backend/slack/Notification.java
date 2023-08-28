package nomad.backend.slack;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nomad.backend.member.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member booker;

    @Column(nullable = false)
    private String location;

    @Column
    private int roomFloor;

    public Notification(Member booker, String location) {
        this.booker = booker;
        this.location = location;
    }

    public Notification(Member booker, String location, int roomFloor) {
        this.booker = booker;
        this.location = location;
        this.roomFloor = roomFloor;
    }
}
