package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.Client;
import net.parkvision.parkvisionbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<User, Long> {
}
