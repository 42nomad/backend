package nomad.backend.history;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nomad.backend.member.Member;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historyId;
    @Column(nullable = false)
    private String location;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (nullable = false)
    private Member cadet;
    @Column(nullable = false)
    private String date;

    History(String location, Member cadet, String date) {
        this.location = location;
        this.cadet = cadet;
        this.date = date;
    }
}
