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
public class ParkingService {

    private final ParkingRepository _parkingRepository;
    private final ParkingSpotRepository _parkingSpotRepository;
    private final ReservationService _reservationService;

    @Autowired
    public ParkingService(ParkingRepository parkingRepository, ParkingSpotRepository parkingSpotRepository, ReservationService reservationService) {
        this._parkingRepository = parkingRepository;
        _parkingSpotRepository = parkingSpotRepository;
        _reservationService = reservationService;
    }

    public List<Parking> getAllParkings() {
        return _parkingRepository.findAll();
    }

    public Optional<Parking> getParkingById(Long id) {
        return _parkingRepository.findById(id);
    }

    public Parking createParking(Parking parking) {
        return _parkingRepository.save(parking);
    }

    public Parking updateParking(Parking parking) {
        if (!_parkingRepository.existsById(parking.getId())) {
            throw new IllegalArgumentException("Parking with ID " + parking.getId() + " does not exist.");
        }

        return _parkingRepository.save(parking);
    }

    public void deleteParking(Long id) {
        _parkingRepository.deleteById(id);
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
}
