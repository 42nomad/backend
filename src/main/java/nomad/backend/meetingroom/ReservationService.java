package nomad.backend.meetingroom;

import jakarta.annotation.PostConstruct;
import nomad.backend.global.exception.custom.InternalServerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservationService {

    public List <ReservationDto> getReservationList() {
        List<ReservationDto> reservationList = new ArrayList<ReservationDto>();
        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
        LocalDateTime koreaDateTime = LocalDateTime.now(koreaZoneId);
        Integer year = koreaDateTime.getYear();
        Integer month = koreaDateTime.getMonthValue();
        Integer today = koreaDateTime.getDayOfMonth();

        String URL = "https://innovationacademy.kr/academy/space/view?level=2&menuNo=11&year=" + year + "&month=" + month;
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch(IOException e) {
            e.printStackTrace();
        }
        String day;
        String content;
        String revTitle;
        String revLocation;
        String revTime;
        List<Element> elements = doc.select("td");
        for(Element element : elements) {
            day 	=  element.getElementsByClass("day").text();
            content =  element.getElementsByClass("cont").text();
            if (day != "" && (today == Integer.valueOf(day))) {
                String[] reservations = content.split("\\(승인\\)|\\(승인대기\\)"); // 숭인 또는 승인대기 기준으로 자른다.
                for (String reservation :reservations) { // 형식 : 3층 세미나실 장소 : 새롬관 3층 시간 : 10:00 ~ 13:59
                    String[] infos = reservation.trim().split(" 장소 : | 시간 : ");
                    if (infos.length % 3 != 0) {
                        throw new InternalServerException();
                    }
                    revTitle = infos[0];
                    revLocation = infos[1];
                    revTime = infos[2];
                    ReservationDto reservationDto = new ReservationDto(revTitle, revLocation, revTime);
                    reservationList.add(reservationDto);
                }
            }
        }
        Collections.sort(reservationList, new Comparator<ReservationDto>()  {
            @Override
            public int compare(ReservationDto o1, ReservationDto o2) {
                int nameComparison = o1.getRevTitle().compareTo(o2.getRevTitle());
                if (nameComparison != 0) {
                    return nameComparison;
                }
                // 예약된 시간으로 정렬
                return o1.getRevTime().compareTo(o2.getRevTime());
            }
        });
        return reservationList;
    }
}
