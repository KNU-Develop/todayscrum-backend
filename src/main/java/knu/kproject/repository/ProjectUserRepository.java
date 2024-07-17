package knu.kproject.repository;

import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    List<ProjectUser> findByUserId(String userId);

    List<ProjectUser> findByProjectId(Long projectId);
}
