package net.parkvision.parkvisionbackend.service;

import jakarta.transaction.Transactional;
import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class ParkingSpotService {

    private final ParkingSpotRepository _parkingSpotRepository;
    private final ParkingRepository _parkingRepository;
    private final ReservationService _reservationService;
    private final PointService _pointService;

    @Autowired
    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository,
                              ParkingRepository parkingRepository,
                              ReservationService reservationService,
                              PointService pointService) {
        _parkingSpotRepository = parkingSpotRepository;
        _parkingRepository = parkingRepository;
        _reservationService = reservationService;
        _pointService = pointService;
    }


    public List<ParkingSpot> getAllParkingSpots() {
        return _parkingSpotRepository.findAll();
    }

    public List<ParkingSpot> getAllParkingSpotsWithPoints() {
        List<ParkingSpot> parkingSpots = _parkingSpotRepository.findAll();
        return getSpotsWithPoints(parkingSpots);
    }

    public Optional<ParkingSpot> getParkingSpotById(Long id) {
        return _parkingSpotRepository.findById(id);
    }

    public ParkingSpot createParkingSpot(ParkingSpot parkingSpot) {
        if (!_parkingRepository.existsById(parkingSpot.getParking().getId())) {
            throw new IllegalArgumentException("Parking with ID " + parkingSpot.getParking().getId() + " does not " +
                    "exist.");
        }
        if (!parkingSpot.getPoints().isEmpty()) {
            for (int i = 0; i < parkingSpot.getPoints().size(); i++) {
                parkingSpot.getPoints().get(i).setParkingSpot(parkingSpot);
            }
        }
        return _parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot updateParkingSpot(ParkingSpot parkingSpot) {
        if (!_parkingSpotRepository.existsById(parkingSpot.getId())) {
            throw new IllegalArgumentException("ParkingSpot with ID " + parkingSpot.getId() + " does not exist.");
        }

        if (!_parkingRepository.existsById(parkingSpot.getParking().getId())) {
            throw new IllegalArgumentException("Parking with ID " + parkingSpot.getParking().getId() + " does not " +
                    "exist.");
        }
        parkingSpot.setPoints(parkingSpot.getPoints());

        for (int i = 0; i < parkingSpot.getPoints().size(); i++) {
            parkingSpot.getPoints().get(i).setParkingSpot(parkingSpot);
            _pointService.updatePoint(parkingSpot.getPoints().get(i));
        }

        parkingSpot.setSpotNumber(parkingSpot.getSpotNumber());
        parkingSpot.setActive(parkingSpot.isActive());
        parkingSpot.setParking(parkingSpot.getParking());

        return _parkingSpotRepository.save(parkingSpot);
    }

    @Transactional
    public void softDeleteParkingSpot(Long id) {
        _parkingSpotRepository.findById(id).ifPresent(parkingSpot -> {
            List<Reservation> futureReservations = _reservationService.getFutureReservationByParkingSpotId(id);
            if(!futureReservations.isEmpty()){
                throw new IllegalArgumentException("ParkingSpot with ID " + id + " has future reservations.");
            }
            parkingSpot.setActive(false);
            List<Point> points = _pointService.getPointsByParkingSpotId(id);
            for (Point point : points) {
                _pointService.deletePoint(point.getId());
            }
            _parkingSpotRepository.save(parkingSpot);
        });
    }

    public void hardDeleteParkingSpot(Long id) {
        _parkingSpotRepository.deleteById(id);
    }

    public List<ParkingSpot> getFreeSpots(Parking parking, OffsetDateTime startDate, OffsetDateTime endDate) {
        List<ParkingSpot> freeParkingSpots = new ArrayList<>();
        List<ParkingSpot> activeParkingSpotList = getActiveParkingSpots(parking);

        for (ParkingSpot parkingSpot : activeParkingSpotList) {
            Reservation reservation = new Reservation();
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            reservation.setParkingSpot(parkingSpot);
            if (_reservationService.isParkingSpotFree(reservation)) {
                freeParkingSpots.add(parkingSpot);
            }
        }

        return freeParkingSpots;
    }

    public List<ParkingSpot> getParkingSpots(Parking parking) {
        return new ArrayList<>(_parkingSpotRepository.findByParkingId(parking.getId()));
    }

    public List<ParkingSpot> getParkingSpotsWithPoints(Parking parking) {
        List<ParkingSpot> parkingSpots = new ArrayList<>(_parkingSpotRepository.findByParkingId(parking.getId()));
        List<ParkingSpot> parkingSpotsWithPoints = getSpotsWithPoints(parkingSpots);
        return new ArrayList<>(parkingSpotsWithPoints);
    }

    private List<ParkingSpot> getSpotsWithPoints(List<ParkingSpot> parkingSpots) {
        List<ParkingSpot> parkingSpotsWithPoints = new ArrayList<>();
        for (ParkingSpot parkingSpot : parkingSpots) {
            List<Point> points = _pointService.getPointsByParkingSpotId(parkingSpot.getId());
            if(!points.isEmpty()) {
                parkingSpotsWithPoints.add(parkingSpot);
            }
        }
        return parkingSpotsWithPoints;
    }

    public List<ParkingSpot> getActiveParkingSpots(Parking parking) {
        return new ArrayList<>(_parkingSpotRepository.findByParkingId(parking.getId()).stream()
                .filter(ParkingSpot::isActive)
                .toList());
    }

    public List<ParkingSpot> createParkingSpots(List<ParkingSpot> parkingSpotList) {
        List<ParkingSpot> parkingSpotsResponse = new ArrayList<>();
        if (!parkingSpotList.isEmpty()) {
            for (ParkingSpot parkingSpot : parkingSpotList) {
                parkingSpotsResponse.add(createParkingSpot(parkingSpot));
            }
        }
        return parkingSpotsResponse;
    }

    public Map<Long, Map<String, OffsetDateTime>> getSpotsFreeTime(Parking parking, OffsetDateTime date) {
        Map<Long, Map<String, OffsetDateTime>> parkingSpotsWhenFree = new HashMap<>();
        List<ParkingSpot> activeParkingSpotList = getActiveParkingSpots(parking);

        date = date.withOffsetSameInstant(parking.getTimeZone()); //TODO redundant

        for (ParkingSpot parkingSpot : activeParkingSpotList) {
            parkingSpotsWhenFree.put(parkingSpot.getId(),
                    _reservationService.getEarliestAvailableTime(parkingSpot, date));
        }
        return parkingSpotsWhenFree;
    }

    public List<ParkingSpot> getParkingSpots(Drone drone) {
        return new ArrayList<>(_parkingSpotRepository.findByParkingId(drone.getParking().getId()).stream().filter(parkingSpot ->
                !parkingSpot.getPoints().isEmpty() && parkingSpot.isActive()).toList());
    }
}
