package nomad.backend.starred;

import lombok.RequiredArgsConstructor;
import nomad.backend.imac.IMac;
import nomad.backend.member.Member;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StarredService {

    private final StarredRepository starredRepository;

    public List<Starred> getMemberStarredList(Member member) {
        List<Starred> starredList = starredRepository.findByOwner(member);
        return starredList;
    }

    public Starred registerStar(Member member, IMac iMac) {
        Starred star = new Starred(member, iMac);
        starredRepository.save(star);
        return star;
    }
}
