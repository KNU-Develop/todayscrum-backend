package knu.kproject.repository;

import knu.kproject.entity.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepositroy extends JpaRepository<Project, UUID> {
    List<Project> findByWorkspaceOwnerId(Long userId);
}
