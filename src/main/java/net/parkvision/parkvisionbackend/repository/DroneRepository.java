package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.Drone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {
}
