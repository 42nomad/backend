package nomad.backend.admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CredentialsRepository {
    private final EntityManager em;

    @Transactional
    public void insertCredential(String credentialType, String data) {
        try {
            Credentials credential = findByCredentialType(credentialType);
            credential.updateCredentialInfo(data);
        } catch (NoResultException e) {
            Credentials credential = new Credentials(credentialType, data);
            em.persist(credential);
        }
    }

    public Credentials findByCredentialType(String credentialType) {
        return em.createQuery("SELECT c FROM Credentials c WHERE c.credentialType = :credentialType", Credentials.class)
                .setParameter("credentialType", credentialType)
                .getSingleResult();
    }
}
