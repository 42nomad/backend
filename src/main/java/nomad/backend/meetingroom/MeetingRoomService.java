package nomad.backend.meetingroom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    public List<MeetingRoomDto> getMeetingRoomInfoByFloor(int floor) {
        List<MeetingRoom> meetingRoomList = meetingRoomRepository.getMeetingRoomInfoByFloor(floor);
        Date now = new Date();
        return meetingRoomList.stream()
                .map(m -> {
                    int usageTime = m.getStatus() ?
                            (int) ((now.getTime() - m.getStartTime().getTime()) / (1000 * 60)) : -1;
                    return new MeetingRoomDto(m.getLocation(), m.getStatus(), usageTime);
                })
                .collect(Collectors.toList());
    }

    // To Do: iot에 따라 형식 변화 필요. 필요시 스케쥴링 도입.
    @Transactional
    public void updateMeetingRoomStatus(MeetingRoom location, boolean status) {
        // location별 신호 분기?
        if (status)
            location.updateStatus(new Date());
        else
            location.updateStatus(); // 통계 적용 시 사용시간 로그 혹은 DB 남기기
    }

    // csv 파일 경로 어디에 둘 지 생각해보기
    @Transactional
    public void loadCsvDataToDatabase() throws IOException, ParseException {
        try (BufferedReader br = new BufferedReader(new FileReader("./src/main/java/nomad/backend/meetingroom/meetingRoom.csv", Charset.forName("UTF-8")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                MeetingRoom room = new MeetingRoom(Integer.parseInt(data[0]), data[1]);
                meetingRoomRepository.save(room);
            }
        }
    }
}