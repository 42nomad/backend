package nomad.backend.starred;

import lombok.RequiredArgsConstructor;
import nomad.backend.global.exception.ConflictException;
import nomad.backend.imac.IMac;
import nomad.backend.imac.IMacDto;
import nomad.backend.imac.IMacService;
import nomad.backend.member.Member;
import nomad.backend.slack.Notification;
import nomad.backend.slack.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class StarredService {

    private final StarredRepository starredRepository;
    private final IMacService iMacService;

    private final NotificationService notificationService;
    public List<StarredDto> getMemberStarredList(Member member) {
        List<Starred> starredList = starredRepository.findByOwner(member);

        return starredList.stream()
                .map(starred -> {
                    IMacDto iMacDto = iMacService.parseIMac(starred.getLocation());
                    Notification notification = notificationService.findByMemberAndIMacLocation(member, iMacDto.getLocation());
                    Boolean isNoti = Boolean.FALSE;
                    Long notificationId = 0L;
                    if (notification != null) {
                        isNoti = Boolean.TRUE;
                        notificationId = notification.getNotificationId();
                    }
                    return new StarredDto(starred.getStarredId(), iMacDto.getLocation(), iMacDto.getCadet(), iMacDto.getElapsedTime(), iMacDto.getIsAvailable(),isNoti, notificationId);
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public Starred registerStar(Member owner, IMac iMac) {
        Starred star = starredRepository.findByOwnerAndLocation(owner, iMac);
        if(star == null)
        {
            star = new Starred(owner, iMac);
            return starredRepository.save(star);
        }
        else{ throw new ConflictException();}

    }
    @Transactional
    public void deleteStar(Long starredId) {
        Starred starred = starredRepository.findByStarredId(starredId);
        if (starred != null) {
            Notification notification = notificationService.findByMemberAndIMacLocation(starred.getOwner(), starred.getLocation().getLocation());
            if (notification != null) {
                notificationService.deleteNotification(notification.getNotificationId());
            }
        }
        starredRepository.deleteByStarredId(starredId);
    }

    public boolean isStarred(Member member, IMac iMac) {
        return starredRepository.findByOwnerAndLocation(member, iMac) != null;
    }
}
