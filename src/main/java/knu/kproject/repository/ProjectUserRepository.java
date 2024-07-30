package knu.kproject.repository;

import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    List<ProjectUser> findByProjectId(Long projectId);
    ProjectUser findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
}
