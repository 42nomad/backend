package nomad.backend.starred;

import jakarta.persistence.*;
import lombok.Getter;
import nomad.backend.imac.IMac;
import nomad.backend.member.Member;

@Entity
@Getter
public class Starred {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer starred_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imac_id", nullable = false)
    private IMac location;
}
