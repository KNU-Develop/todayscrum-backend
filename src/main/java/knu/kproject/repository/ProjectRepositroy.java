package knu.kproject.repository;

import knu.kproject.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepositroy extends JpaRepository<Project, Long> {
    List<Project> findByWorkspaceId(Long workspaceId);
}
