package knu.kproject.repository;

import knu.kproject.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface StackRepository extends JpaRepository<Stack, Long> {
    Optional<Stack> findByName(String name);
}
