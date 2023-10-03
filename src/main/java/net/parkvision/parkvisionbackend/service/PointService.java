package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.dto.PointDTO;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.model.Point;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import net.parkvision.parkvisionbackend.repository.PointRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PointService {

    private final PointRepository _pointRepository;
    private final ParkingSpotRepository _parkingSpotRepository;
    private final ModelMapper modelMapper;

    public PointService(PointRepository pointRepository, ParkingSpotRepository parkingSpotRepository, ModelMapper modelMapper) {
        _pointRepository = pointRepository;
        _parkingSpotRepository = parkingSpotRepository;
        this.modelMapper = modelMapper;
    }

    public List<Point> getAllPoints() {
        return _pointRepository.findAll();
    }

    public Optional<Point> getPointById(Long id) {
        return _pointRepository.findById(id);
    }

    public Point createPoint(PointDTO pointDto){

        ParkingSpot parkingSpot = _parkingSpotRepository.findById(pointDto.getParkingSpotId()).orElseThrow(
                () -> new IllegalArgumentException("ParkingSpot with ID " + pointDto.getParkingSpotId() + " does not exist.")
        );

        Point point = modelMapper.map(pointDto, Point.class);
        point.setParkingSpot(parkingSpot);

        return _pointRepository.save(point);
    }

    public Point updatePoint(Long id, PointDTO pointDto){
        Point point = _pointRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Point with ID " + id + " does not exist.")
        );

        ParkingSpot parkingSpot = _parkingSpotRepository.findById(pointDto.getParkingSpotId()).orElseThrow(
                () -> new IllegalArgumentException("ParkingSpot with ID " + pointDto.getParkingSpotId() + " does not exist.")
        );

        point.setParkingSpot(parkingSpot);
        point.setLatitude(pointDto.getLatitude());
        point.setLongitude(pointDto.getLongitude());

        return _pointRepository.save(point);
    }

    public void deletePoint(Long id) {
        _pointRepository.deleteById(id);
    }


}
