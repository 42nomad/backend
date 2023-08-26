package nomad.backend.starred;

import lombok.RequiredArgsConstructor;
import nomad.backend.board.Board;
import nomad.backend.board.BoardDto;
import nomad.backend.imac.IMac;
import nomad.backend.imac.IMacDto;
import nomad.backend.imac.IMacService;
import nomad.backend.member.Member;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StarredService {

    private final StarredRepository starredRepository;
    private final IMacService iMacService;

    public List<StarredDto> getMemberStarredList(Member member) {
        List<Starred> starredList = starredRepository.findByOwner(member);

        return starredList.stream()
                .map(starred -> {
                    IMacDto iMacDto = iMacService.parseIMac(starred.getLocation());
                    return new StarredDto(starred.getStarredId(), iMacDto.getLocation(), iMacDto.getCadet(), iMacDto.getElapsedTime());
                })
                .collect(Collectors.toList());
    }

    public Starred registerStar(Member owner, IMac iMac) {
        Starred star = starredRepository.findByOwnerAndLocation(owner, iMac);
        if(star == null)
        {
            star = new Starred(owner, iMac);
            starredRepository.save(star);
        }
        return star;
    }

    public void deleteStar(Integer id) {
        starredRepository.deleteByStarredId(id);
    }

    public boolean isStarred(Member member, IMac iMac) {
        if (starredRepository.findByOwnerAndLocation(member, iMac) == null)
            return false;
        return true;
    }
}
