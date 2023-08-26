package nomad.backend.history;

import nomad.backend.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {
    List<History> findAllByIntra(String intra);
}
