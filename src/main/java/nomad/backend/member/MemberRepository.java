package nomad.backend.member;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public Optional<Member> findById(Long memberId) {
        Member member = new Member("hyunjcho");
        return Optional.of(member);
    }

    public Optional<Member> findByRefreshToken(String refreshToken) {
        Member member = new Member("hyunjcho");
        return Optional.of(member);
    }
}
