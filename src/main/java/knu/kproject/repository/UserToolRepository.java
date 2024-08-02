package knu.kproject.repository;


import knu.kproject.entity.Tool;
import knu.kproject.entity.User;
import knu.kproject.entity.UserTool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserToolRepository extends JpaRepository<UserTool, Long> {
    Optional<UserTool> findByUserAndTool(User user, Tool tool);
}