package nomad.backend.starred;

import lombok.RequiredArgsConstructor;
import nomad.backend.board.Board;
import nomad.backend.board.BoardDto;
import nomad.backend.imac.IMac;
import nomad.backend.member.Member;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StarredService {

    private final StarredRepository starredRepository;

    public List<StarredDto> getMemberStarredList(Member member) {
        List<Starred> starredList = starredRepository.findByOwner(member);

        return starredList.stream()
                .map(starred -> new StarredDto(starred.getStarredId(), starred.getOwner().getIntra(), starred.getLocation().getLocation()))
                .collect(Collectors.toList());
    }

    public Starred registerStar(Member owner, IMac iMac) {
        Starred star = new Starred(owner, iMac);
        starredRepository.save(star);
        return star;
    }

    public void deleteStar(Integer id) {
        starredRepository.deleteByStarredId(id);
    }
}
