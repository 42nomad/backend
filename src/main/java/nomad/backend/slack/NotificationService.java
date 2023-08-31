package nomad.backend.slack;

import lombok.RequiredArgsConstructor;
import nomad.backend.global.exception.custom.ConflictException;
import nomad.backend.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Long saveIMacNotification(Member member, String location) {
        Notification noti = notificationRepository.findByMemberAndIMacLocation(member, location);
        if (noti != null)
            throw new ConflictException();
        return notificationRepository.save(new Notification(member, location));
    }

    public Long saveMeetingRoomNotification(Member member, String cluster, String location) {
        Notification noti = notificationRepository.findByMemberAndRoomLocation(member, cluster, location);
        if (noti != null)
            throw new ConflictException();
        return notificationRepository.save(new Notification(member, cluster, location));
    }

    public Notification findByMemberAndIMacLocation(Member member, String location) {
        return notificationRepository.findByMemberAndIMacLocation(member, location);
    }

    public Notification findByMemberAndRoomLocatiton(Member member, String location, String cluster) {
        return notificationRepository.findByMemberAndRoomLocation(member, location, cluster);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
