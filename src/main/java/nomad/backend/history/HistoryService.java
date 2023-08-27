package nomad.backend.history;

import lombok.RequiredArgsConstructor;
import nomad.backend.member.Member;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HistoryService {
    private final HistoryRepository historyRepository;

    public void addHistory(String location,Member member, String startTime) {
        History history = new History(location, member, startTime);
        historyRepository.save(history);
    }

}
