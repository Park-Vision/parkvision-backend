package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.ParkingManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingManagerRepository extends JpaRepository<ParkingManager, Long> {
}
