package nomad.backend.meetingroom;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MeetingRoomRepository {
    private final EntityManager em;

    public void save(MeetingRoom meetingRoom) {
        em.persist(meetingRoom);
    }
    public List<MeetingRoom> getMeetingRoomInfoByCluster(String cluster) {
        return em.createQuery("SELECT m FROM MeetingRoom m WHERE m.cluster = :cluster", MeetingRoom.class)
                .setParameter("cluster", cluster)
                .getResultList();
    }

    public MeetingRoom getMeetingRoomInfoByClusterAndLocation(String cluster, String location) {
        return em.createQuery("SELECT m FROM MeetingRoom m WHERE m.cluster = :cluster AND m.location = :location", MeetingRoom.class)
                .setParameter("cluster", cluster)
                .setParameter("location", location)
                .getSingleResult();
    }
}
