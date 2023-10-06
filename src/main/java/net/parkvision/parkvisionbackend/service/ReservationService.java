package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Car;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Reservation createReservation(Reservation reservation) {
        if (!_userRepository.existsById(reservation.getUser().getId())) {
            throw new IllegalArgumentException("Client with ID " + reservation.getUser().getId() + " does not exist.");
        }

        if (!_parkingSpotRepository.existsById(reservation.getParkingSpot().getId())) {
            throw new IllegalArgumentException("ParkingSpot with ID " + reservation.getParkingSpot().getId() + " does" +
                    " not exist.");
        }

        if (reservation.getCar() != null) {
            if (!_carRepository.existsById(reservation.getCar().getId())) {
                throw new IllegalArgumentException("Car with ID " + reservation.getCar().getId() + " does not exist.");
            }
        }

        return _reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Reservation reservation) {
        if (!_reservationRepository.existsById(reservation.getId())) {
            throw new IllegalArgumentException("Reservation with ID " + reservation.getId() + " does not exist.");
        }

        Car car = null;

        if (!_userRepository.existsById(reservation.getUser().getId())) {
            throw new IllegalArgumentException("Client with ID " + reservation.getUser().getId() + " does not exist.");
        }

        if (!_parkingSpotRepository.existsById(reservation.getParkingSpot().getId())) {
            throw new IllegalArgumentException("ParkingSpot with ID " + reservation.getParkingSpot().getId() + " does" +
                    " not exist.");
        }

        if (reservation.getCar() != null) {
            car = reservation.getCar();
            if (!_carRepository.existsById(reservation.getCar().getId())) {
                throw new IllegalArgumentException("Car with ID " + reservation.getCar().getId() + " does not exist.");
            }
        }

        reservation.setStartDate(reservation.getStartDate());
        reservation.setEndDate(reservation.getEndDate());
        reservation.setRegistrationNumber(reservation.getRegistrationNumber());
        reservation.setUser(reservation.getUser());
        reservation.setParkingSpot(reservation.getParkingSpot());
        if (car != null) {
            reservation.setCar(reservation.getCar());
        }

        return _reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        _reservationRepository.deleteById(id);
    }
}
