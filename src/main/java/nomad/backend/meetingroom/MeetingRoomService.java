package nomad.backend.meetingroom;

import lombok.RequiredArgsConstructor;
import nomad.backend.global.Define;
import nomad.backend.global.exception.custom.NotFoundException;
import nomad.backend.member.Member;
import nomad.backend.slack.Notification;
import nomad.backend.slack.NotificationService;
import nomad.backend.slack.SlackService;
import nomad.backend.statistics.StatService;
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
    private final SlackService slackService;
    private final StatService statService;

    public List<MeetingRoomDto> getMeetingRoomInfoByCluster(String cluster, Member member) {
        List<MeetingRoom> meetingRoomList = meetingRoomRepository.getMeetingRoomInfoByCluster(cluster);
        if (meetingRoomList.isEmpty())
            throw new NotFoundException();
        Date now = new Date();
        return meetingRoomList.stream()
                .map(m -> {
                    int usageTime = m.getStatus() ? calculateUsageTime(m.getStartTime()) : -1;
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
    public void updateMeetingRoomStatus(String cluster, String location) {
        MeetingRoom meetingRoom = meetingRoomRepository.getMeetingRoomInfoByClusterAndLocation(cluster, location);
        Boolean status = meetingRoom.getStatus();
        // location별 신호 분기?
        if (status == Boolean.FALSE) {
            // off 상태일 경우 그때의 시간 기준으로 on 시킨다.
            meetingRoom.updateStatus(new Date());
            slackService.findMeetingRoomNotificationAndSendMessage(cluster, location, cluster + "의 " + location + Define.TAKEN_ROOM);
        }
        else {
            meetingRoom.updateStatus();
            slackService.findMeetingRoomNotificationAndSendMessage(cluster, location, cluster + "의 " + location + Define.EMPTY_ROOM);
            statService.saveStatic(cluster, location, meetingRoom.getStartTime(), calculateUsageTime(meetingRoom.getStartTime()));
        }

    }

    private int calculateUsageTime(Date date) {
        Date now = new Date();
        return (int) ((now.getTime() - date.getTime()) / (1000 * 60));
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
