package knu.kproject.repository;

import knu.kproject.entity.Stack;
import knu.kproject.entity.User;
import knu.kproject.entity.UserStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStackRepository extends JpaRepository<UserStack, Long>{
    Optional<UserStack> findByUserAndStack(User user, Stack stack);
    void deleteByUser(User user);
}
