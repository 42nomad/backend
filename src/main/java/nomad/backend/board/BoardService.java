package nomad.backend.board;

import lombok.RequiredArgsConstructor;
import nomad.backend.global.exception.custom.NotFoundException;
import nomad.backend.imac.IMac;
import nomad.backend.imac.IMacService;
import nomad.backend.member.Member;
import nomad.backend.member.MemberService;
import nomad.backend.slack.SlackService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final IMacService iMacService;
    private final SlackService slackService;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public List<BoardDto> getAllPost() {
        return toBoardDto(boardRepository.findAll());
    }

    public List<BoardDto> toBoardDto(List<Board> boardList) {
        return boardList.stream()
                .map(post -> {
                    String date = simpleDateFormat.format(post.getCreated_at());
                    return new BoardDto(post.getBoardId(), post.getLocation(), post.getImage(), date);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void writePost(Member member, WriteDto post) {
        boardRepository.save(new Board(member, post.getLocation(), post.getContents(), post.getImgUrl()));
        findLeftCadetAndSendMessage(post.getLocation().toLowerCase());
    }

    @Transactional
    public void modifyPost(Long postId, WriteDto post) {
        Board board = boardRepository.findByBoardId(postId);
        if (board == null)
            throw new NotFoundException();
        board.updatePost(post);
    }

    // 누가 로그아웃하고 leftCadet 남는거 확인 후에 그 좌석으로 분실물 올려서 작성되는지 확인 필수
    public void findLeftCadetAndSendMessage(String location) {
        IMac iMac = iMacService.findByLocation(location);
        if (iMac == null || iMac.getLeftCadet() == null)
            return ;
        Member leftCadet = memberService.findByIntra(iMac.getLeftCadet());
        if (leftCadet == null)
            return ;
        slackService.findNotificationAndSendMessage(leftCadet.getIntra(), location, leftCadet.getIntra() + "님(" + location + ")");
    }

    public PostDto getPostInfo(Long memberId, Long postId) throws NullPointerException {
        Board post = boardRepository.findByBoardId(postId);
        if (post == null)
            throw new NotFoundException();
        String date = simpleDateFormat.format(post.getCreated_at());
        boolean isMine = memberId == post.getWriter().getMemberId();
        return new PostDto(postId, post.getWriter().getIntra(), post.getLocation(), post.getContents(), post.getImage(), date, isMine);
    }

    @Transactional
    public void deletePostByPostId(Long postId) {
        boardRepository.deleteBoardByBoardId(postId);
    }

    // To Do : 스케쥴러 한 3분 간격으로 하고 계산도 3분전 이런식으로 해서 테스트 필요 함
    @Scheduled(cron = "0 42 4 * * *")
    public void deleteOldPost() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = calendar.getTime();
        boardRepository.deleteBoardsOlderThan(thirtyDaysAgo);
    }
}
