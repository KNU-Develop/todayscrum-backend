package knu.kproject.repository;

import knu.kproject.entity.Project;
import knu.kproject.entity.ProjectUser;
import knu.kproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    List<ProjectUser> findByProjectId(UUID projectId);

    List<ProjectUser> findByUserId(Long userId);

    ProjectUser findByUserAndProject(User user, Project project);

    boolean existsByProjectAndUser(Project project, User user);
}
