package knu.kproject.repository;

import knu.kproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialId(String email);

    User findByName(String name);

    boolean existsByName(String name);
}
