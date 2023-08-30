package nomad.backend.history;

import lombok.RequiredArgsConstructor;
import nomad.backend.member.Member;
import nomad.backend.member.MemberRepository;
import nomad.backend.member.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addHistory(String location,String intra, String startTime) {
        Member member = memberRepository.findByIntra(intra).orElse(null);
        if (member == null)
            return;
        History history = historyRepository.findByMemberAndLocation(member, location);
        if (history != null) {
            history.updateDate(startTime);
        } else {
            history = new History(location, member, startTime);
            historyRepository.save(history);
        }
    }
}
