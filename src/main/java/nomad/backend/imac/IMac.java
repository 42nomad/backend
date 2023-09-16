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
    private Date loginTime;

    @Column
    private Date logoutTime;

    @Column
    private String leftCadet;

    @Column
    private Date updatedAt;

    public void resetLogoutTime() {
        this.logoutTime = null;
    }

    public IMac(String cluster, String location) {
        this.cluster = cluster;
        this.location = location;
    }

    public void updateLoginCadet(String cadet, Date updateAt, Date loginTime) {
        this.cadet = cadet;
        this.loginTime = loginTime;
        this.logoutTime = null;
        this.updatedAt = updateAt;
    }

    public void updateLoginCadet(Date date) {
        this.updatedAt = date;
    }

    public void updateLogoutCadet(Date logoutTime, String leftCadet) {
        this.cadet = null;
        this.loginTime = null;
        this.logoutTime = logoutTime;
        this.leftCadet = leftCadet;
    }

    public void forceLogout() {
        System.out.println("force 진행 하나요? 자리 = " + this.location);
        this.cadet = null;
        this.logoutTime = null;
        this.leftCadet = null;
    }
}
