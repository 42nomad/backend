package nomad.backend.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nomad.backend.board.Board;
import nomad.backend.history.History;
import nomad.backend.starred.Starred;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String intra;

    @Column
    private String refreshToken;

    @Column
    private Integer home;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<Board> posts = new ArrayList<Board>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Starred> stars = new ArrayList<Starred>();
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<History> histories = new ArrayList<History>();

    public Member(String intra) {
        this.intra = intra;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateHome(Integer home) {
        this.home = home;
    }
}
