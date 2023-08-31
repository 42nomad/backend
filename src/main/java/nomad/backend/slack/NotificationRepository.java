package nomad.backend.slack;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import nomad.backend.member.Member;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {
    public final EntityManager em;

    public Long save(Notification notification) {
        em.persist(notification);
        return notification.getNotificationId();
    }

    public List<Notification> findByIMacLocation(String location) {
        return em.createQuery("SELECT n FROM Notification n WHERE n.location = :location", Notification.class)
                .setParameter("location", location)
                .getResultList();
    }

    public List<Notification> findByClusterAndMeetingRoomLocation(String cluster, String location) {
        return em.createQuery("SELECT n FROM Notification n WHERE n.cluster = :cluster AND n.location = :location", Notification.class)
                .setParameter("cluster", cluster)
                .setParameter("location", location)
                .getResultList();
    }

    public Notification findByMemberAndIMacLocation(Member member, String location) {
        try {
            return em.createQuery("SELECT n FROM Notification n WHERE n.booker = :member AND n.location = :location", Notification.class)
                    .setParameter("member", member)
                    .setParameter("location", location)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Notification findByMemberAndRoomLocation(Member member, String cluster, String location) {
        try {
            return em.createQuery("SELECT n FROM Notification n WHERE n.booker = :member AND n.cluster = :cluster AND n.location = :location ", Notification.class)
                    .setParameter("member", member)
                    .setParameter("cluster", cluster)
                    .setParameter("location", location)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void deleteById(Long notificationId) {
        Notification notification = em.find(Notification.class, notificationId);
        if (notification != null) {
            em.remove(notification);
        }
    }
}
