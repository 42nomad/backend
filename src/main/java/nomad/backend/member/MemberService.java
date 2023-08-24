package nomad.backend.member;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;



    public List<Member> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members;
    }

    public Member getMemberByAuth(Authentication authentication) {
        Member member = memberRepository.findByMemberId(Long.valueOf(authentication.getName())).get();
        return member;
    }

    public Member findByMemberId(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId).get();
        return member;
    }

}
