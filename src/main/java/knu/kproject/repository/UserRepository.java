package knu.kproject.repository;

import knu.kproject.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialId(String email);

    User findByName(String name);

    User findByEmail(String email);

    boolean existsByName(String name);

    boolean existsByEmail(String email);
}
