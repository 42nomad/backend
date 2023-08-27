package nomad.backend.board;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BoardRepository extends CrudRepository<Board, Long> {
    List<Board> findAll();

    Board save(Board board);

    Board findByBoardId(Long id);

    void deleteBoardByBoardId(Long id);

    @Modifying
    @Query("DELETE FROM Board b WHERE b.created_at < :targetDate")
    void deleteBoardsOlderThan(@Param("targetDate") Date targetDate);
}