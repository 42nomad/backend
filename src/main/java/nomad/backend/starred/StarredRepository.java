package nomad.backend.starred;

import nomad.backend.imac.IMac;
import nomad.backend.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StarredRepository extends CrudRepository<Starred, Integer> {
    List<Starred> findByOwner(Member owner);

    Starred findByStarredId(Integer starredId);

    Starred findByOwnerAndLocation(Member owner, IMac location);

    void deleteByStarredId(Integer starredId);
}
