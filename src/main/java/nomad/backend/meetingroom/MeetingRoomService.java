package nomad.backend.meetingroom;

import lombok.RequiredArgsConstructor;
import nomad.backend.global.exception.custom.NotFoundException;
import nomad.backend.member.Member;
import nomad.backend.slack.Notification;
import nomad.backend.slack.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final NotificationService notificationService;

    public List<MeetingRoomDto> getMeetingRoomInfoByCluster(String cluster, Member member) {
        List<MeetingRoom> meetingRoomList = meetingRoomRepository.getMeetingRoomInfoByCluster(cluster);
        if (meetingRoomList.isEmpty())
            throw new NotFoundException();
        Date now = new Date();
        return meetingRoomList.stream()
                .map(m -> {
                    int usageTime = m.getStatus() ?
                            (int) ((now.getTime() - m.getStartTime().getTime()) / (1000 * 60)) : -1;
                    Notification noti = notificationService.findByMemberAndRoomLocatiton(member, cluster, m.getLocation());
                    boolean isNoti = false;
                    Long notificationId = 0L;
                    if (noti != null) {
                        isNoti = true;
                        notificationId = noti.getNotificationId();
                    }
                    return new MeetingRoomDto(m.getLocation(), !m.getStatus(), usageTime, isNoti, notificationId);
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

    @Transactional
    public void loadCsvDataToDatabase() throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader("/home/ec2-user/apps/backend/src/main/java/nomad/backend/meetingroom/meetingRoom.csv", Charset.forName("UTF-8")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                MeetingRoom room = new MeetingRoom(data[0], data[1]);
                meetingRoomRepository.save(room);
            }
        }
    }
}
