package nomad.backend.imac;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IMac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imac_id;

    @Column(nullable = false)
    private String cluster;

    @Column(nullable = false)
    private String location;

    @Column
    private String cadet;

    @Column
    private Date logoutTime;

    @Column
    private String leftCadet;

    public void resetLogoutTime() {
        this.logoutTime = null;
    }

    public IMac(String cluster, String location) {
        this.cluster = cluster;
        this.location = location;
    }

    public void updateLoginCadet(String cadet) {
        this.cadet = cadet;
        this.logoutTime = null;
    }

    public void updateLogoutCadet(Date logoutTime, String leftCadet) {
        this.cadet = null;

        if (this.logoutTime == null || this.logoutTime.before(logoutTime)) { // before 확인필요
            this.logoutTime = logoutTime;
            this.leftCadet = leftCadet;
        }
    }
}
