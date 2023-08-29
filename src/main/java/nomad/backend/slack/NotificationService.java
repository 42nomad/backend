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

    public void saveIMacNotification(Member member, String location) {
        Notification noti = notificationRepository.findByMemberAndIMacLocation(member, location);
        if (noti != null)
            throw new ConflictException();
        notificationRepository.save(new Notification(member, location));
    }

    public void saveMeetingRoomNotification(Member member, String location, String cluster) {
        Notification noti = notificationRepository.findByMemberAndRoomLocation(member, location, cluster);
        if (noti != null)
            throw new ConflictException();
        notificationRepository.save(new Notification(member, location, cluster));
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
