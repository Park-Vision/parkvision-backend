package net.parkvision.parkvisionbackend.repository;

import net.parkvision.parkvisionbackend.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByParkingSpotId(Long parkingSpotId);
}
