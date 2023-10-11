package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingSpotService {

    private final ParkingSpotRepository _parkingSpotRepository;
    private final ParkingRepository _parkingRepository;
    private final ReservationService _reservationService;

    @Autowired
    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository, ParkingRepository parkingRepository, ReservationService reservationService) {
        this._parkingSpotRepository = parkingSpotRepository;
        _parkingRepository = parkingRepository;
        _reservationService = reservationService;
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

    public List<ParkingSpot> getFreeSpots(Parking parking, Date startDate, Date endDate) {
        List<ParkingSpot> freeParkingSpots = new ArrayList<>();

        for(ParkingSpot parkingSpot: _parkingSpotRepository.findByParkingId(parking.getId())){
            Reservation reservation = new Reservation();
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            reservation.setParkingSpot(parkingSpot);
            if(_reservationService.isParkingSpotFree(reservation)){
                freeParkingSpots.add(parkingSpot);
            }
        }

        return freeParkingSpots;
    }

    public List<ParkingSpot> getParkingSpots(Parking parking) {
        return new ArrayList<>(_parkingSpotRepository.findByParkingId(parking.getId()));
    }
}
