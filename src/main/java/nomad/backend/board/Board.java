package nomad.backend.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nomad.backend.IMac.IMac;
import nomad.backend.member.Member;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Board {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long board_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imac_id")
    private IMac iMac;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private Date created_at;

    public Board(Member member, IMac iMac, String contents, String image) {
        this.writer = member;
        this.iMac = iMac;
        this.contents = contents;
        this.image = image;
        this.created_at = new Date();
    }
}
