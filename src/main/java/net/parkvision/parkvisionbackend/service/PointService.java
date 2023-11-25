package net.parkvision.parkvisionbackend.service;

import jakarta.transaction.Transactional;
import net.parkvision.parkvisionbackend.model.Point;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import net.parkvision.parkvisionbackend.repository.PointRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PointService {

    private final PointRepository pointRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    public PointService(PointRepository pointRepository, ParkingSpotRepository parkingSpotRepository) {
        this.pointRepository = pointRepository;
        this.parkingSpotRepository = parkingSpotRepository;
    }

    public List<Point> getAllPoints() {
        return pointRepository.findAll();
    }

    public Optional<Point> getPointById(Long id) {
        return pointRepository.findById(id);
    }

    public List<Point> getPointsByParkingSpotId(Long parkingSpotId) {
        List<Point> points = pointRepository.findByParkingSpotId(parkingSpotId);
        points.sort((p1, p2) -> (int) (p1.getId() - p2.getId()));
        return points;
    }

    public Point createPoint(Point point) {
        if (!parkingSpotRepository.existsById(point.getParkingSpot().getId())) {
            throw new IllegalArgumentException("ParkingSpot with ID " + point.getParkingSpot().getId() + " does not " +
                    "exist.");
        }

        return pointRepository.save(point);
    }

    public Point updatePoint(Point point) {
        if (!pointRepository.existsById(point.getId())) {
            throw new IllegalArgumentException("Point with ID " + point.getId() + " does not exist.");
        }

        if (!parkingSpotRepository.existsById(point.getParkingSpot().getId())) {
            throw new IllegalArgumentException("ParkingSpot with ID " + point.getParkingSpot().getId() + " does not " +
                    "exist.");
        }

        point.setLatitude(point.getLatitude());
        point.setLongitude(point.getLongitude());
        point.setParkingSpot(point.getParkingSpot());

        return pointRepository.save(point);
    }

    @Transactional
    public void deletePoint(Long id) {
        pointRepository.deleteById(id);
    }
}
