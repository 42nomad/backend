package nomad.backend.board;

import lombok.RequiredArgsConstructor;
import nomad.backend.member.Member;
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
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public List<BoardDto> getAllPost() {
        List<Board> boardList = boardRepository.findAll();

        return boardList.stream()
                .map(post -> {
                    String date = simpleDateFormat.format(post.getCreated_at());
                    return new BoardDto(post.getBoardId(), post.getLocation(), post.getImage(), date);
                })
                .collect(Collectors.toList());
    }

    public List<BoardDto> toPostDto(List<Board> boardList) {
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
    }

    @Transactional
    public void modifyPost(Long postId, WriteDto post) {
        Board board = boardRepository.findByBoardId(postId);
        board.updatePost(post);
    }

    public PostDto getPostInfo(Long memberId, Long postId) throws NullPointerException {
        Board post = boardRepository.findByBoardId(postId);
        if (post == null) // postman으로 요청하는 경우에 해당하는 예외처리를 하는 것이 좋을지?
            throw new NullPointerException(); // exception class
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
