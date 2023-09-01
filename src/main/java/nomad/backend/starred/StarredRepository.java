package nomad.backend.starred;

import nomad.backend.imac.IMac;
import nomad.backend.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StarredRepository extends CrudRepository<Starred, Long> {
    List<Starred> findByOwner(Member owner);

    Starred findByStarredId(Long starredId);

    Starred findByOwnerAndLocation(Member owner, IMac location);

    void deleteByStarredId(Long starredId);
}
