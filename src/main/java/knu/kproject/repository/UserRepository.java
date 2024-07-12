package knu.kproject.repository;

import knu.kproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class UserRepository implements JpaRepository<User, Long> {
}
