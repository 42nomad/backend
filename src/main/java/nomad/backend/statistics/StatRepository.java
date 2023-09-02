package nomad.backend.statistics;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatRepository {
    public final EntityManager em;

    public void save(Stat stat) {
        em.persist(stat);
    }

    public List<Stat> findByUsedDate(int type, Date startDate, Date endDate) {
        return em.createQuery("SELECT s FROM Stat s WHERE s.type = :type AND s.usedDate >= :startDate AND s.usedDate <= :endDate ORDER BY s.cluster ASC, s.location ASC", Stat.class)
                .setParameter("type", type)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    public List<Stat> findByClusterAndUsedDate(int type, String cluster, Date startDate, Date endDate) {
        return em.createQuery("SELECT s FROM Stat s WHERE s.type = :type AND s.cluster = :cluster AND s.usedDate >= :startDate AND s.usedDate <= :endDate ORDER BY s.cluster ASC, s.location ASC", Stat.class)
                .setParameter("type", type)
                .setParameter("cluster", cluster)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
}
