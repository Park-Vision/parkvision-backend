package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.Drone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {
    List<Drone> findAllByParkingId(Long parkingId);

    Optional<Drone> findByName(String name);
}
