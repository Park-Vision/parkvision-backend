package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByParkingSpotId(Long parkingSpotId);
}
