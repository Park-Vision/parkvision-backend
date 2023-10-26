package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.exception.ReservationConflictException;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import net.parkvision.parkvisionbackend.repository.ReservationRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.*;

@Service
public class ReservationService {

    private final ReservationRepository _reservationRepository;

    private final UserRepository _userRepository;
    private final ParkingSpotRepository _parkingSpotRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository,
                              ParkingSpotRepository parkingSpotRepository) {
        _reservationRepository = reservationRepository;
        _userRepository = userRepository;
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

        if (!isParkingSpotFree(reservation)) {
            throw new ReservationConflictException("Konflikt datowy z istniejącą rezerwacją.");
        }

        return _reservationRepository.save(reservation);
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

    public ZonedDateTime getEarliestAvailableTime(ParkingSpot parkingSpot, ZonedDateTime date) {
        List<Reservation> reservations = _reservationRepository.findByParkingSpotId(parkingSpot.getId())
                .stream()
                .filter(reservation -> reservation.getEndDate().isAfter(date))
                .filter(reservation -> reservation.getEndDate().getDayOfMonth() == date.getDayOfMonth())
                .filter(reservation -> reservation.getEndDate().getMonth() == date.getMonth())
                .filter(reservation -> reservation.getEndDate().getYear() == date.getYear())
                .sorted(Comparator.comparing(Reservation::getEndDate)).toList();

        ZonedDateTime parkingEndTime = parkingSpot.getParking().getEndTime().toLocalTime()
                .atDate(date.toLocalDate())
                .atZone(date.getZone());

        ZonedDateTime parkingStartTime = parkingSpot.getParking().getStartTime().toLocalTime()
                .atDate(date.toLocalDate())
                .atZone(date.getZone());

        ZonedDateTime earliestAvailableTime = date;
        if (earliestAvailableTime.isBefore(parkingStartTime)) {
            earliestAvailableTime = parkingStartTime;
        }

        for (Reservation reservation : reservations) {
            if (earliestAvailableTime.plusMinutes(30).isBefore(reservation.getStartDate())) {
                return earliestAvailableTime;
            }
            earliestAvailableTime = reservation.getEndDate();
        }
        if (earliestAvailableTime.plusMinutes(30).isBefore(parkingEndTime)) {
            return earliestAvailableTime;
        }
        return null;
    }

    public Map<String, List<Reservation>> getSortedReservationsByClient(User client) {
        List<Reservation> clientReservations = _reservationRepository.findByUserId(client.getId());
        Map<String, List<Reservation>> clientSortedReservations = new HashMap<>();
        clientSortedReservations.put("Pending", new ArrayList<>());
        clientSortedReservations.put("Archived", new ArrayList<>());

        ZonedDateTime actualTime = ZonedDateTime.now();

        for (Reservation reservation : clientReservations) {
            String category = reservation.getEndDate().isAfter(actualTime) ? "Pending" : "Archived";
            clientSortedReservations.get(category).add(reservation);
        }

        clientSortedReservations.get("Pending").sort(Comparator.comparing(Reservation::getEndDate));

        clientSortedReservations.get("Archived").sort(Comparator.comparing(Reservation::getEndDate).reversed());

        return clientSortedReservations;
    }
}
