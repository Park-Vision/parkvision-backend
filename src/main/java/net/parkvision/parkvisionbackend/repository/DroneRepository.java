package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.Drone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {
    public List<Drone> findAllByParkingId(Long parkingId);
}
