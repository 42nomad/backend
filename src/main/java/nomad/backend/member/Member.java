package nomad.backend.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nomad.backend.board.Board;
import nomad.backend.starred.Starred;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_id;

    @Column(nullable = false)
    private String intra;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<Board> posts = new ArrayList<Board>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Starred> stars = new ArrayList<Starred>();

    public Member(String intra) {
        this.intra = intra;
    }
}
