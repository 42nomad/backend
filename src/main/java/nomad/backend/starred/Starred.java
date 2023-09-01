package nomad.backend.starred;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nomad.backend.imac.IMac;
import nomad.backend.member.Member;

@Entity
@Getter
@RequiredArgsConstructor
public class Starred {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long starredId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imacId", nullable = false)
    private IMac location;

    Starred(Member member, IMac iMac) {
        this.owner = member;
        this.location = iMac;
    }
}
