package nomad.backend.history;

import nomad.backend.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.xml.stream.Location;
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {
    History findByMemberAndLocation(Member member, String location);
}
