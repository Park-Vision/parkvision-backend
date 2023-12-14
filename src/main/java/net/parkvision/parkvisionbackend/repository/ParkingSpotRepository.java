package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    List<ParkingSpot> findByParkingId(Long parkingId);

    ParkingSpot findBySpotNumber(String spotNumber);
}
