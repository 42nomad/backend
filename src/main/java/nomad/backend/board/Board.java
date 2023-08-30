package nomad.backend.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nomad.backend.member.Member;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Board {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private Date created_at;

    public Board(Member member, String location, String contents, String image) {
        this.writer = member;
        this.location = location;
        this.contents = contents;
        this.image = image;
        this.created_at = new Date();
    }

    public void updatePost(WriteDto post) {
        this.location = post.getLocation();
        this.contents = post.getContents();
        this.image = post.getImgKey();
    }
}
