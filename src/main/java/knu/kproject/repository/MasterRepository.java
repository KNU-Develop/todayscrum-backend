package knu.kproject.repository;

import knu.kproject.entity.board.Board;
import knu.kproject.entity.board.Master;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MasterRepository extends JpaRepository<Master, UUID> {
    List<Master> findByBoard(Board boardId);
}
