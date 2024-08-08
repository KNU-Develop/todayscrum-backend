package knu.kproject.repository;

import knu.kproject.entity.Board;
import knu.kproject.entity.Master;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MasterRepository extends JpaRepository<knu.kproject.entity.Master, UUID> {
    List<Master> findByBoardId(Board boardId);
}
