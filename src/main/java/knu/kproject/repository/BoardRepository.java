package knu.kproject.repository;

import knu.kproject.entity.Board;
import knu.kproject.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, UUID> {
    List<Board> findByProject(Project project);
}
