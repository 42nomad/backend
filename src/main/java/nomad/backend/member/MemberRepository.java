package nomad.backend.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIntra(String intra);
    Optional<Member> findByMemberId(Long memberId);
    Optional<Member> findByRefreshToken(String refreshToken);

}
