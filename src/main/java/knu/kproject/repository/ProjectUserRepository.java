package knu.kproject.repository;

import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    List<ProjectUser> findByProjectId(Long projectId);
    ProjectUser findByUserId(UUID userId);
}
