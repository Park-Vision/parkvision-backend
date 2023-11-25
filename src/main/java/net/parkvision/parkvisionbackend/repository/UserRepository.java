package net.parkvision.parkvisionbackend.repository;

import java.util.Optional;

import net.parkvision.parkvisionbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPasswordResetToken(String token);
}
