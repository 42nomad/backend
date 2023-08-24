package nomad.backend.starred;

import nomad.backend.imac.IMac;
import nomad.backend.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StarredRepository extends JpaRepository<Starred, Integer> {
    List<Starred> findByOwner(Member owner);

    void deleteByStarredId(Integer id);
}
