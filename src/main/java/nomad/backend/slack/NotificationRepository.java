package nomad.backend.slack;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import nomad.backend.member.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {
    public final EntityManager em;

    public void save(Notification notification) {
        em.persist(notification);
    }

    @Query("SELECT n FROM Notification n WHERE n.location = :location")
    List<Notification> findByLocation(@Param("location") String location) {
        return null;
    }

    @Query("SELECT n FROM Notification n WHERE n.owner = :member AND n.location = :location")
    Notification findCustomByMemberAndLocation(@Param("member") Member member, @Param("location") String location) {
        return null;
    }
}
