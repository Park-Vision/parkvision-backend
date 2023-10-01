package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.ParkingModerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingModeratorRepository extends JpaRepository<ParkingModerator, Long> {
}
