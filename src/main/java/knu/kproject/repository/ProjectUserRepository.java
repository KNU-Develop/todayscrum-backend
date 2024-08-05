package knu.kproject.repository;

import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    List<ProjectUser> findByProjectId(UUID projectId);
    ProjectUser findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    boolean existsByProjectIdAndUserId(UUID projectId, Long userId);
}
