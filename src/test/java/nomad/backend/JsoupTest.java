package nomad.backend;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class JsoupTest {
    public static void main(String[] args) {

        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
        LocalDateTime koreaDateTime = LocalDateTime.now(koreaZoneId);
        Integer year = koreaDateTime.getYear();
        Integer month = koreaDateTime.getMonthValue();
        Integer today = koreaDateTime.getDayOfMonth();
//        Integer month = 9;
//        Integer today = 4;

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
                    revTitle = infos[0];
                    revLocation = infos[1];
                    revTime = infos[2];
                    System.out.println("Title = " + revTitle + " Location = " + revLocation + " Time = " + revTime);
                }
            }
        }
    }
}
