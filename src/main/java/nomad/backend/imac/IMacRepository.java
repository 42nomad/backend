package nomad.backend.imac;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class IMacRepository {
    private final EntityManager em;

    public void save(IMac iMac) {
        em.persist(iMac);
    }

    public List<IMac> findLoginCadetByCluster(String cluster) {
        return em.createQuery("SELECT i FROM IMac i WHERE i.cluster = :cluster AND i.cadet IS NOT NULL", IMac.class)
                .setParameter("cluster", cluster)
                .getResultList();
    }

    public List<IMac> findByCluster(String cluster) {
        return em.createQuery("SELECT i FROM IMac i WHERE i.cluster = :cluster AND ((i.cadet IS NOT NULL) OR (i.cadet IS NULL AND i.logoutTime IS NOT NULL))", IMac.class)
                .setParameter("cluster", cluster)
                .getResultList();
    }

    public IMac findByLocation(String location) {
        try {
            return em.createQuery("SELECT i FROM IMac i WHERE i.location = :location", IMac.class)
                    .setParameter("location", location)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<IMac> findByCadetAndUpdatedAt(Date now) {
        return em.createQuery("SELECT i FROM IMac i WHERE i.cadet IS NOT NULL AND i.updatedAt <> :now", IMac.class)
                .setParameter("now", now)
                .getResultList();
    }
}
