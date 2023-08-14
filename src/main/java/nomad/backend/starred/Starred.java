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
    @JoinColumn(name = "member_id")
    @Column(nullable = false)
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imac_id")
    @Column(nullable = false)
    private IMac location;
}
