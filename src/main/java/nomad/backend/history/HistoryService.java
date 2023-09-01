package nomad.backend.history;

import lombok.RequiredArgsConstructor;
import nomad.backend.member.Member;
import nomad.backend.member.MemberRepository;
import nomad.backend.member.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            try {
                Date oldDate = sdf.parse(history.getDate());
                Date newDate = sdf.parse(startTime);
                //Date 객체를 Calender 객체로 바꿔서 월, 일을 비교하는 부분
                Calendar oldCalendar = Calendar.getInstance();
                oldCalendar.setTime(oldDate);
                Calendar newCalendar = Calendar.getInstance();
                newCalendar.setTime(newDate);
                boolean sameYear = oldCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR);
                boolean sameMonth = oldCalendar.get(Calendar.MONTH) == newCalendar.get(Calendar.MONTH);
                boolean sameDay = oldCalendar.get(Calendar.DAY_OF_MONTH) == newCalendar.get(Calendar.DAY_OF_MONTH);
                if (sameYear && sameMonth && sameDay) {
                    // 같은 날일 경우에는 시간만 업데이트해준다.
                    history.updateDate(startTime);
                } else {
                    // 아닐경우 새로운 History 객체를 만들어서 저장한다.
                    history = new History(location, member, startTime);
                    historyRepository.save(history);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            history = new History(location, member, startTime);
            historyRepository.save(history);
        }
    }
}
