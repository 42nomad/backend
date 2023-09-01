package nomad.backend.statics;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StaticRepository {
    public final EntityManager em;

    public void save(Statics statics) {
        em.persist(statics);
    }

    public List<Statics> findByUsedDate(Date startDate, Date endDate) {
        return em.createQuery("SELECT s FROM Statics s WHERE s.usedDate >= :startDate AND s.usedDate <= :endDate ORDER BY s.cluster ASC, s.location ASC", Statics.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
}
