package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Point;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import net.parkvision.parkvisionbackend.repository.PointRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PointService {

    private final PointRepository _pointRepository;
    private final ParkingSpotRepository _parkingSpotRepository;

    public PointService(PointRepository pointRepository, ParkingSpotRepository parkingSpotRepository) {
        _pointRepository = pointRepository;
        _parkingSpotRepository = parkingSpotRepository;
    }

    public List<Point> getAllPoints() {
        return _pointRepository.findAll();
    }

    public Optional<Point> getPointById(Long id) {
        return _pointRepository.findById(id);
    }

    public Point createPoint(Point point) {
        if (!_parkingSpotRepository.existsById(point.getParkingSpot().getId())) {
            throw new IllegalArgumentException("ParkingSpot with ID " + point.getParkingSpot().getId() + " does not " +
                    "exist.");
        }

        return _pointRepository.save(point);
    }

    public Point updatePoint(Point point) {
        if (!_pointRepository.existsById(point.getId())) {
            throw new IllegalArgumentException("Point with ID " + point.getId() + " does not exist.");
        }

        if (!_parkingSpotRepository.existsById(point.getParkingSpot().getId())) {
            throw new IllegalArgumentException("ParkingSpot with ID " + point.getParkingSpot().getId() + " does not " +
                    "exist.");
        }

        point.setLatitude(point.getLatitude());
        point.setLongitude(point.getLongitude());
        point.setParkingSpot(point.getParkingSpot());

        return _pointRepository.save(point);
    }

    public void deletePoint(Long id) {
        _pointRepository.deleteById(id);
    }

}
