package knu.kproject.repository;


import knu.kproject.entity.ToolName;
import knu.kproject.entity.User;
import knu.kproject.entity.UserTool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserToolRepository extends JpaRepository<UserTool, Long> {
    Optional<UserTool> findByUserAndTool(User user, ToolName tool);
}