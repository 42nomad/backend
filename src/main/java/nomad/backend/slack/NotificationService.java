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

    public void saveMeetingRoomNotification(Member member, String location, int floor) {
        Notification noti = notificationRepository.findByMemberAndRoomLocation(member, location, floor);
        if (noti != null)
            throw new ConflictException();
        notificationRepository.save(new Notification(member, location, floor));
    }

    public Notification findByMemberAndIMacLocation(Member member, String location) {
        return notificationRepository.findByMemberAndIMacLocation(member, location);
    }

    public Notification findByMemberAndRoomLocatiton(Member member, String location, int floor) {
        return notificationRepository.findByMemberAndRoomLocation(member, location, floor);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
