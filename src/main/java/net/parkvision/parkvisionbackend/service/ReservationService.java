package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.exception.ReservationConflictException;
import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import net.parkvision.parkvisionbackend.repository.ReservationRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.*;

@Service
public class ReservationService {

    private final ReservationRepository _reservationRepository;

    private final ParkingRepository _parkingRepository;

    private final UserRepository _userRepository;
    private final ParkingSpotRepository _parkingSpotRepository;
    private final StripeChargeService _stripeChargeService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository,
                              ParkingSpotRepository parkingSpotRepository, ParkingRepository parkingRepository,
                              StripeChargeService stripeChargeService) {
        _reservationRepository = reservationRepository;
        _userRepository = userRepository;
        _parkingSpotRepository = parkingSpotRepository;
        _parkingRepository = parkingRepository;
        _stripeChargeService = stripeChargeService;
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

        if (!isWithinParkingHours(startDate, endDate, parking)) {
            throw new IllegalArgumentException("Rezerwacja nie mieści się w godzinach otwarcia parkingu.");
        }
        if (!checkTime(startDate, endDate)) {
            throw new IllegalArgumentException("Rezerwacja niewlasciwa czasowo.");
        }

        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);

        Reservation createdReservation = _reservationRepository.save(reservation);
        createdReservation.getParkingSpot().setParking(parking);
        return createdReservation;
    }

    private boolean checkTime(OffsetDateTime start, OffsetDateTime end) {
        return start.toLocalTime().isAfter(OffsetDateTime.now().withOffsetSameInstant(start.getOffset()).minusMinutes(15).toLocalTime())
                && start.toLocalTime().isBefore(end.toLocalTime());
    }

    private boolean isWithinParkingHours(OffsetDateTime start, OffsetDateTime end, Parking parking) {
        OffsetTime parkingStart = parking.getStartTime();
        OffsetTime parkingEnd = parking.getEndTime();

        return start.toLocalTime().isAfter(parkingStart.toLocalTime())
                && start.toLocalTime().isBefore(parkingEnd.toLocalTime())
                && end.toLocalTime().isAfter(parkingStart.toLocalTime())
                && end.toLocalTime().isBefore(parkingEnd.toLocalTime());
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
        reservation.setAmount(reservation.getAmount());

        return _reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        _reservationRepository.deleteById(id);
    }

    public Reservation cancelReservation(Long id) {
        Optional<StripeCharge> stripeCharge = _stripeChargeService.getStripeChargeByReservationId(id);
        if (stripeCharge.isPresent()) {
            StripeCharge refundedCharge = _stripeChargeService.refundCharge(stripeCharge.get().getId());
            if (refundedCharge.getSuccess()) {
                refundedCharge.setReservation(null);
                StripeCharge updatedCharge = _stripeChargeService.updateStripeCharge(refundedCharge);
                if (updatedCharge.getReservation() == null) {
                    Reservation canceledReservation = _reservationRepository.getReferenceById(id);
                    deleteReservation(id);
                    return canceledReservation;
                }
            }
        }
        return null;
    }

    public Map<String, OffsetDateTime> getEarliestAvailableTime(ParkingSpot parkingSpot, OffsetDateTime date) {
        List<Reservation> reservations = _reservationRepository.findByParkingSpotId(parkingSpot.getId())
                .stream()
                .filter(reservation -> reservation.getEndDate().isAfter(date))
                .filter(reservation -> reservation.getEndDate().getDayOfMonth() == date.getDayOfMonth())
                .filter(reservation -> reservation.getEndDate().getMonth() == date.getMonth())
                .filter(reservation -> reservation.getEndDate().getYear() == date.getYear())
                .sorted(Comparator.comparing(Reservation::getEndDate)).toList();

        OffsetDateTime parkingEndTime = OffsetDateTime.of(
                date.toLocalDate(),
                parkingSpot.getParking().getEndTime().toLocalTime(),
                parkingSpot.getParking().getEndTime().getOffset()
        );

        OffsetDateTime parkingStartTime = OffsetDateTime.of(
                date.toLocalDate(),
                parkingSpot.getParking().getStartTime().toLocalTime(),
                parkingSpot.getParking().getStartTime().getOffset()
        );

        OffsetDateTime earliestAvailableTime = OffsetDateTime.of(date.toLocalDateTime(),
                parkingSpot.getParking().getTimeZone());
        if (earliestAvailableTime.isBefore(parkingStartTime)) {
            earliestAvailableTime = parkingStartTime;
        }

        for (Reservation reservation : reservations) {
            //TODO add 15 break.
            if (earliestAvailableTime.plusMinutes(15).isBefore(reservation.getStartDate())) {
                Map<String, OffsetDateTime> map = new HashMap<>();
                map.put("earliestStart", earliestAvailableTime);
                map.put("earliestEnd", reservation.getStartDate());
                return map;
            }
            earliestAvailableTime = reservation.getEndDate();
        }
        if (earliestAvailableTime.isBefore(parkingEndTime)) {
            Map<String, OffsetDateTime> map = new HashMap<>();
            map.put("earliestStart", earliestAvailableTime);
            map.put("earliestEnd",
                    parkingEndTime);
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

    public List<Reservation> getAllReservationsByParkingSpot(Long id) {
        return _reservationRepository.findAll()
                .stream()
                .filter(reservation -> Objects.equals(reservation.getParkingSpot().getId(), id))
                .sorted(Comparator.comparing(Reservation::getEndDate))
                .toList();
    }

    public List<Reservation> getFutureReservationByParkingSpot(Long id) {
        return _reservationRepository.findAll()
                .stream()
                .filter(reservation -> Objects.equals(reservation.getParkingSpot().getId(), id))
                .filter(reservation -> reservation.getStartDate().isAfter(OffsetDateTime.now()))
                .sorted(Comparator.comparing(Reservation::getEndDate))
                .toList();
    }
}
