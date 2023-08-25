package nomad.backend.imac;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class IMacRepository {
    private final EntityManager em;

    public void save(IMac iMac) {
        em.persist(iMac);
    }

    // 사용 중인 경우만 반환
    public List<IMac> findLoginCadetByCluster(String cluster) {
        return em.createQuery("select i from IMac i where i.cluster = :cluster and i.cadet is not null", IMac.class)
                .setParameter("cluster", cluster)
                .getResultList();
    }

    // 사용 중인 경우 및 로그인 상태는 아니지만 로그아웃타임 정보가 있는 경우
    public List<IMac> findByCluster(String cluster) {
        return em.createQuery("select i from IMac i where i.cluster = :cluster and ((i.cadet is not null) or (i.cadet is null and i.logoutTime is not null))", IMac.class)
                .setParameter("cluster", cluster)
                .getResultList();
    }

    public IMac findByLocation(String location) {
        try {
            return em.createQuery("select i from IMac i where i.location = :location", IMac.class)
                    .setParameter("location", location)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
