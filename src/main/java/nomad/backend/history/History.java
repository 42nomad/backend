package nomad.backend.history;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Column(nullable = false)
    private String intra;
    @Column(nullable = false)
    private Date date;
}
