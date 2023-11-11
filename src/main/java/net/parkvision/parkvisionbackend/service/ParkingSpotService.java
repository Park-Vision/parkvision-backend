package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
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
        this._parkingSpotRepository = parkingSpotRepository;
        _parkingRepository = parkingRepository;
        _reservationService = reservationService;
        _pointService = pointService;
    }


    public List<ParkingSpot> getAllParkingSpots() {
        return _parkingSpotRepository.findAll();
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
        // edit each point
        for (int i = 0; i < parkingSpot.getPoints().size(); i++) {
            parkingSpot.getPoints().get(i).setParkingSpot(parkingSpot);
            _pointService.updatePoint(parkingSpot.getPoints().get(i));
        }

        parkingSpot.setSpotNumber(parkingSpot.getSpotNumber());
        parkingSpot.setOccupied(parkingSpot.isOccupied());
        parkingSpot.setActive(parkingSpot.isActive());
        parkingSpot.setParking(parkingSpot.getParking());

        return _parkingSpotRepository.save(parkingSpot);
    }

    // so we will use soft delete instead of hard delete
    public void softDeleteParkingSpot(Long id) {
        _parkingSpotRepository.findById(id).ifPresent(parkingSpot -> {
            parkingSpot.setActive(false);
            _parkingSpotRepository.save(parkingSpot);
        });
    }

    public void hardDeleteParkingSpot(Long id) {
        _parkingSpotRepository.deleteById(id);
    }

    public List<ParkingSpot> getFreeSpots(Parking parking, ZonedDateTime startDate, ZonedDateTime endDate) {
        List<ParkingSpot> freeParkingSpots = new ArrayList<>();
        List<ParkingSpot> activeParkingSpotList = getActiveParkingSpots(parking);

        for (ParkingSpot parkingSpot : activeParkingSpotList) {
            Reservation reservation = new Reservation();
            reservation.setStartDate(startDate.toOffsetDateTime());
            reservation.setEndDate(endDate.toOffsetDateTime());
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

    public Map<Long, Map<String, ZonedDateTime>> getSpotsFreeTime(Parking parking, ZonedDateTime date) {
        Map<Long, Map<String, ZonedDateTime>> parkingSpotsWhenFree = new HashMap<>();
        List<ParkingSpot> activeParkingSpotList = getActiveParkingSpots(parking);

        for (ParkingSpot parkingSpot : activeParkingSpotList) {
            parkingSpotsWhenFree.put(parkingSpot.getId(),
                    _reservationService.getEarliestAvailableTime(parkingSpot, date));
        }
        return parkingSpotsWhenFree;
    }

    public List<ParkingSpot> getParkingSpots(Drone drone) {
        return new ArrayList<>(_parkingSpotRepository.findByParkingId(drone.getParking().getId()));
    }
}
