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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import static java.lang.Float.parseFloat;

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
        OffsetDateTime startDate = reservation.getStartDate().withOffsetSameInstant(parking.getTimeZone());
        OffsetDateTime endDate = reservation.getEndDate().withOffsetSameInstant(parking.getTimeZone());

        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);

        Duration duration = Duration.between(startDate, endDate);
        long minutes = duration.toMinutes();
        double amount = minutes * parking.getCostRate() / 60.0;
        BigDecimal roundedAmount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);

        reservation.setAmount(roundedAmount.doubleValue());
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

        ParkingSpot parkingSpot = _parkingSpotRepository.getReferenceById(reservation.getParkingSpot().getId());

        Parking parking = _parkingRepository.getReferenceById(parkingSpot.getParking().getId());

        OffsetDateTime startDate = reservation.getStartDate().withOffsetSameInstant(parking.getTimeZone());
        OffsetDateTime endDate = reservation.getEndDate().withOffsetSameInstant(parking.getTimeZone());

        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setRegistrationNumber(reservation.getRegistrationNumber());
        reservation.setUser(reservation.getUser());
        reservation.setParkingSpot(reservation.getParkingSpot());

        return _reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        _reservationRepository.deleteById(id);
    }

    public Map<String, ZonedDateTime> getEarliestAvailableTime(ParkingSpot parkingSpot, ZonedDateTime date) {
        List<Reservation> reservations = _reservationRepository.findByParkingSpotId(parkingSpot.getId())
                .stream()
                .filter(reservation -> reservation.getEndDate().isAfter(date.toOffsetDateTime()))
                .filter(reservation -> reservation.getEndDate().getDayOfMonth() == date.getDayOfMonth())
                .filter(reservation -> reservation.getEndDate().getMonth() == date.getMonth())
                .filter(reservation -> reservation.getEndDate().getYear() == date.getYear())
                .sorted(Comparator.comparing(Reservation::getEndDate)).toList();

        OffsetDateTime parkingEndTime = parkingSpot.getParking().getEndTime().toLocalTime()
                .atDate(date.toLocalDate())
                .atZone(date.getZone()).toOffsetDateTime();

        OffsetDateTime parkingStartTime = parkingSpot.getParking().getStartTime().toLocalTime()
                .atDate(date.toLocalDate())
                .atZone(date.getZone()).toOffsetDateTime();

        ZonedDateTime earliestAvailableTime = date;
        if (earliestAvailableTime.isBefore(parkingStartTime.toZonedDateTime())) {
            earliestAvailableTime =
                    parkingStartTime.atZoneSameInstant(parkingSpot.getParking().getTimeZone().normalized());
        }

        for (Reservation reservation : reservations) {
            if (earliestAvailableTime.isBefore(reservation.getStartDate().toZonedDateTime())) {
                Map<String, ZonedDateTime> map = new HashMap<>();
                map.put("earliestStart", earliestAvailableTime);
                map.put("earliestEnd", reservation.getStartDate().toZonedDateTime());
                return map;
            }
            earliestAvailableTime = reservation.getEndDate().toZonedDateTime();
        }
        if (earliestAvailableTime.isBefore(parkingEndTime.toZonedDateTime())) {
            Map<String, ZonedDateTime> map = new HashMap<>();
            map.put("earliestStart", earliestAvailableTime);
            map.put("earliestEnd",
                    parkingEndTime.atZoneSameInstant(parkingSpot.getParking().getTimeZone().normalized()));
            return map;
        }
        return null;
    }

    public Map<String, List<Reservation>> getSortedReservationsByClient(User client) {
        List<Reservation> clientReservations = _reservationRepository.findByUserId(client.getId());
        Map<String, List<Reservation>> clientSortedReservations = new HashMap<>();
        clientSortedReservations.put("Pending", new ArrayList<>());
        clientSortedReservations.put("Archived", new ArrayList<>());

        OffsetDateTime actualTime = OffsetDateTime.now(); //TODO

        for (Reservation reservation : clientReservations) {
            String category = reservation.getEndDate().isAfter(actualTime) ? "Pending" : "Archived";
            clientSortedReservations.get(category).add(reservation);
        }

        clientSortedReservations.get("Pending").sort(Comparator.comparing(Reservation::getEndDate));

        clientSortedReservations.get("Archived").sort(Comparator.comparing(Reservation::getEndDate).reversed());

        return clientSortedReservations;
    }

    public List<Reservation> getAllReservationsByParking(Long id) {
        return _reservationRepository.findAll()
                .stream()
                .filter(reservation -> Objects.equals(reservation.getParkingSpot().getParking().getId(), id))
                .sorted(Comparator.comparing(Reservation::getEndDate))
                .toList();
    }
}
