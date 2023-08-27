package nomad.backend.admin;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Credentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int credentialId;

    @Column(nullable = false)
    private String credentialType;

    @Column(nullable = false)
    private String data;

    @Column
    Date createdAt;

    public Credentials(String credentialType, String data) {
        this.credentialType = credentialType;
        this.data = data;
        this.createdAt = new Date();
    }
    public void updateCredentialInfo(String data) {
        this.data = data;
        this.createdAt = new Date();
    }
}
