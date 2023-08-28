package nomad.backend.member;


import lombok.RequiredArgsConstructor;
import nomad.backend.imac.IMac;
import nomad.backend.imac.IMacDto;
import nomad.backend.imac.IMacService;
import nomad.backend.starred.StarredService;
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
    private final IMacService iMacService;
    private final StarredService starredService;




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

    public Member findByIntra(String intra) {
        Member member = memberRepository.findByIntra(intra).get();
        return member;
    }

    public void updateMemberHome(Member member, Integer home) {
        member.updateHome(home);
        memberRepository.saveAndFlush(member);
    }

    public SearchLocationDto searchLocation(Member member, IMac iMac) {
        IMacDto iMacDto = iMacService.parseIMac(iMac);
        boolean isStarred = starredService.isStarred(member, iMac);
        return new SearchLocationDto(iMacDto.getLocation(), iMacDto.getCadet(), iMacDto.getElapsedTime(), isStarred, iMacDto.getStatus());
    }


}
