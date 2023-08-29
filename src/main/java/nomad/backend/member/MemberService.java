package nomad.backend.member;


import lombok.RequiredArgsConstructor;
import nomad.backend.global.exception.custom.NotFoundException;
import nomad.backend.imac.IMac;
import nomad.backend.imac.IMacDto;
import nomad.backend.imac.IMacService;
import nomad.backend.starred.StarredService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final IMacService iMacService;
    private final StarredService starredService;




    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberByAuth(Authentication authentication) {
        return memberRepository.findByMemberId(Long.valueOf(authentication.getName())).orElse(null);
    }

    public Member findByMemberId(Long memberId) {
        return memberRepository.findByMemberId(memberId).orElse(null);
    }

    public Member findByIntra(String intra) {
        return memberRepository.findByIntra(intra).orElse(null);
    }
    @Transactional
    public void updateMemberHome(Member member, Integer home) {
        if (home > 3 || home < 0)
            throw new NotFoundException();
        member.updateHome(home);
    }

    public SearchLocationDto searchLocation(Member member, IMac iMac) {
        IMacDto iMacDto = iMacService.parseIMac(iMac);
        boolean isStarred = starredService.isStarred(member, iMac);
        return new SearchLocationDto(iMacDto.getLocation(), iMacDto.getCadet(), iMacDto.getElapsedTime(), isStarred, iMacDto.getStatus());
    }


}
