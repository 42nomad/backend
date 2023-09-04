package nomad.backend.member;

import jakarta.persistence.*;
import lombok.*;
import nomad.backend.board.Board;
import nomad.backend.history.History;
import nomad.backend.slack.Notification;
import nomad.backend.starred.Starred;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Column
    private String role;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<Board> posts = new ArrayList<Board>();
    @OrderBy("location asc ")
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Starred> stars = new ArrayList<Starred>();
    @OrderBy("date desc")
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<History> histories = new ArrayList<History>();

    @OneToMany(mappedBy = "booker", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    public Member(String intra) {
        this.intra = intra;
        this.role = "ROLE_USER";
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateHome(Integer home) {
        this.home = home;
    }

    public void updateRole(String role) {
        this.role = role;
    }

}
