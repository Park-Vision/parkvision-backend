package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.exception.ReservationConflictException;
import net.parkvision.parkvisionbackend.model.Car;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class ReservationService {

    private final ReservationRepository _reservationRepository;

    private final UserRepository _userRepository;
    private final CarRepository _carRepository;
    private final ParkingSpotRepository _parkingSpotRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository,
                              CarRepository carRepository, ParkingSpotRepository parkingSpotRepository) {
        _reservationRepository = reservationRepository;
        _userRepository = userRepository;
        _carRepository = carRepository;
        _parkingSpotRepository = parkingSpotRepository;
    }

    public List<Reservation> getAllReservations() {
        return _reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return _reservationRepository.findById(id);
    }

    public Reservation createReservation(Reservation reservation) throws ReservationConflictException {
        if (!_userRepository.existsById(reservation.getUser().getId())) {
            throw new IllegalArgumentException("User with ID " + reservation.getUser().getId() + " does not exist.");
        }

        if (!_parkingSpotRepository.existsById(reservation.getParkingSpot().getId())) {
            throw new IllegalArgumentException("ParkingSpot with ID " + reservation.getParkingSpot().getId() + " does" +
                    " not exist.");
        }

        if (!_parkingSpotRepository.getReferenceById(reservation.getParkingSpot().getId()).isActive()) {
            throw new IllegalArgumentException("ParkingSpot with ID " + reservation.getParkingSpot().getId() +
                    " is not active.");
        }

        if (!isParkingSpotFree(reservation)){
            throw new ReservationConflictException("Konflikt datowy z istniejącą rezerwacją.");
        }

        return _reservationRepository.save(reservation);
    }

    public boolean isParkingSpotFree(Reservation reservation){
        List<Reservation> existingReservations =
                _reservationRepository.findByParkingSpotId(reservation.getParkingSpot().getId());
        for (Reservation existingReservation : existingReservations) {
            if (isDateRangeOverlap(existingReservation, reservation)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDateRangeOverlap(Reservation existingReservation, Reservation newReservation) {

        return newReservation.getStartDate().isBefore(existingReservation.getEndDate())
                && newReservation.getEndDate().isAfter(existingReservation.getStartDate());
    }

    public Reservation updateReservation(Reservation reservation) {
        if (!_reservationRepository.existsById(reservation.getId())) {
            throw new IllegalArgumentException("Reservation with ID " + reservation.getId() + " does not exist.");
        }

        if (!_userRepository.existsById(reservation.getUser().getId())) {
            throw new IllegalArgumentException("Client with ID " + reservation.getUser().getId() + " does not exist.");
        }

        if (!_parkingSpotRepository.existsById(reservation.getParkingSpot().getId())) {
            throw new IllegalArgumentException("ParkingSpot with ID " + reservation.getParkingSpot().getId() + " does" +
                    " not exist.");
        }

        reservation.setStartDate(reservation.getStartDate());
        reservation.setEndDate(reservation.getEndDate());
        reservation.setRegistrationNumber(reservation.getRegistrationNumber());
        reservation.setUser(reservation.getUser());
        reservation.setParkingSpot(reservation.getParkingSpot());

        return _reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        _reservationRepository.deleteById(id);
    }
}
