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
        return em.createQuery("select m from MeetingRoom m where m.cluster = :cluster", MeetingRoom.class)
                .setParameter("cluster", cluster)
                .getResultList();
    }
}
