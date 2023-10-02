package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
}
