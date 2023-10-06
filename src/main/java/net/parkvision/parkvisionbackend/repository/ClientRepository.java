package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
