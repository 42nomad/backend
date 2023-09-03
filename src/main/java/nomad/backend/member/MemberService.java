package nomad.backend.member;


import lombok.RequiredArgsConstructor;
import nomad.backend.global.exception.NotFoundException;
import nomad.backend.history.HistoryDto;
import nomad.backend.imac.IMac;
import nomad.backend.imac.IMacDto;
import nomad.backend.imac.IMacService;
import nomad.backend.starred.StarredService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    @Transactional
    public void deleteMember(Member member) {
        memberRepository.delete(member);
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

    @Transactional
    public void updateMemberRole(Member member, Integer role) {
        switch (role) {
            case 2:
                System.out.println("admin 으로 변경");
                member.updateRole("ROLE_SUPER_ADMIN");
                break;
            case 1:
                System.out.println("staff 으로 변경");
                member.updateRole("ROLE_ADMIN");
                break;
            case 0:
                System.out.println("user 으로 변경");
                member.updateRole("ROLE_USER");
                break;
        }
    }

    public SearchLocationDto searchLocation(Member member, IMac iMac) {
        IMacDto iMacDto = iMacService.parseIMac(iMac);
        boolean isStarred = starredService.isStarred(member, iMac);
        return new SearchLocationDto(iMacDto.getLocation(), iMacDto.getCadet(), iMacDto.getElapsedTime(), isStarred, iMacDto.getIsAvailable());
    }

    public List<HistoryDto> getHistoryList(Member member) {
        return member.getHistories().stream()
                .map(history -> {
                    // 이렇게 ? 아니면 아이맥 조인?
                    IMac iMac = iMacService.findByLocation(history.getLocation());
                    if (iMac != null) {
                        IMacDto iMacDto = iMacService.parseIMac(iMac);
                        //5개만 띄우는 로직 추가해야함
                        return new HistoryDto(history.getLocation(), iMacDto.getCadet(), iMacDto.getElapsedTime(), iMacDto.getIsAvailable(), timeCalculate(history.getDate()));
                    } else {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    private String timeCalculate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date inputDate = sdf.parse(dateString);
            Date currentDate = new Date();
            long timeDifference = currentDate.getTime() - inputDate.getTime();
            long days = timeDifference / (1000 * 60 * 60 * 24);
            long hours = (timeDifference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (timeDifference % (1000 * 60 * 60)) / (1000 * 60);

            if (days > 0) {
                return (days + "일");
            }
            if (hours > 0) {
                return (hours + "시간");
            }
            return (minutes + "분");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "알 수 없음";
    }

}
