package knu.kproject.repository;

import knu.kproject.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class ProjectRepository implements JpaRepository<Project, Long> {
}
