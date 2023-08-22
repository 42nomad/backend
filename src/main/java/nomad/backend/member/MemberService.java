package nomad.backend.member;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    public String getMemberIntra(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        return member.getIntra();
    }





}
