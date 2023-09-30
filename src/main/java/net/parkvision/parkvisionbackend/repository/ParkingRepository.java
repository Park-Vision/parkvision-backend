package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {
}
