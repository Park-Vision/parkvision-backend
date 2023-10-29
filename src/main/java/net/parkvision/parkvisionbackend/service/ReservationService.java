package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.exception.ReservationConflictException;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import net.parkvision.parkvisionbackend.repository.ReservationRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReservationService {

    private final ReservationRepository _reservationRepository;

    private final ParkingRepository _parkingRepository;

    private final UserRepository _userRepository;
    private final ParkingSpotRepository _parkingSpotRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository,
                              ParkingSpotRepository parkingSpotRepository, ParkingRepository parkingRepository) {
        _reservationRepository = reservationRepository;
        _userRepository = userRepository;
        _parkingSpotRepository = parkingSpotRepository;
        _parkingRepository = parkingRepository;
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

        if (!isParkingSpotFree(reservation)) {
            throw new ReservationConflictException("Konflikt datowy z istniejącą rezerwacją.");
        }

        ParkingSpot parkingSpot = _parkingSpotRepository.getReferenceById(reservation.getParkingSpot().getId());

        Parking parking = _parkingRepository.getReferenceById(parkingSpot.getParking().getId());

        Reservation createdReservation = _reservationRepository.save(reservation);
        createdReservation.getParkingSpot().setParking(parking);
        return createdReservation;
    }

    public boolean isParkingSpotFree(Reservation reservation) {
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

    public Map<String, LocalDateTime> getEarliestAvailableTime(ParkingSpot parkingSpot, LocalDateTime date) {
        List<Reservation> reservations = _reservationRepository.findByParkingSpotId(parkingSpot.getId())
                .stream()
                .filter(reservation -> reservation.getEndDate().isAfter(date))
                .filter(reservation -> reservation.getEndDate().getDayOfMonth() == date.getDayOfMonth())
                .filter(reservation -> reservation.getEndDate().getMonth() == date.getMonth())
                .filter(reservation -> reservation.getEndDate().getYear() == date.getYear())
                .sorted(Comparator.comparing(Reservation::getEndDate)).toList();

        LocalDateTime parkingEndTime = parkingSpot.getParking().getEndTime().toLocalTime()
                .atDate(date.toLocalDate());

        LocalDateTime parkingStartTime = parkingSpot.getParking().getStartTime().toLocalTime()
                .atDate(date.toLocalDate());

        LocalDateTime earliestAvailableTime = date;
        if (earliestAvailableTime.isBefore(parkingStartTime)) {
            earliestAvailableTime = parkingStartTime;
        }

        for (Reservation reservation : reservations) {
            if (earliestAvailableTime.isBefore(reservation.getStartDate())) {
                Map<String, LocalDateTime> map = new HashMap<>();
                map.put("earliestStart", earliestAvailableTime);
                map.put("earliestEnd", reservation.getStartDate());
                return map;
            }
            earliestAvailableTime = reservation.getEndDate();
        }
        if (earliestAvailableTime.isBefore(parkingEndTime)) {
            Map<String, LocalDateTime> map = new HashMap<>();
            map.put("earliestStart", earliestAvailableTime);
            map.put("earliestEnd", parkingEndTime);
            return map;
        }
        return null;
    }

    public Map<String, List<Reservation>> getSortedReservationsByClient(User client) {
        List<Reservation> clientReservations = _reservationRepository.findByUserId(client.getId());
        Map<String, List<Reservation>> clientSortedReservations = new HashMap<>();
        clientSortedReservations.put("Pending", new ArrayList<>());
        clientSortedReservations.put("Archived", new ArrayList<>());

        LocalDateTime actualTime = LocalDateTime.now();

        for (Reservation reservation : clientReservations) {
            String category = reservation.getEndDate().isAfter(actualTime) ? "Pending" : "Archived";
            clientSortedReservations.get(category).add(reservation);
        }

        clientSortedReservations.get("Pending").sort(Comparator.comparing(Reservation::getEndDate));

        clientSortedReservations.get("Archived").sort(Comparator.comparing(Reservation::getEndDate).reversed());

        return clientSortedReservations;
    }
}
