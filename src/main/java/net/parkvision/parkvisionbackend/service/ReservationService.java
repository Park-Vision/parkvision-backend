package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.exception.ReservationConflictException;
import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import net.parkvision.parkvisionbackend.repository.ReservationRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.*;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ParkingRepository parkingRepository;

    private final UserRepository userRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final StripeChargeService stripeChargeService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository,
                              ParkingSpotRepository parkingSpotRepository, ParkingRepository parkingRepository,
                              StripeChargeService stripeChargeService) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingRepository = parkingRepository;
        this.stripeChargeService = stripeChargeService;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation createReservation(Reservation reservation) throws ReservationConflictException {
        if (!userRepository.existsById(reservation.getUser().getId())) {
            throw new IllegalArgumentException("User with ID " + reservation.getUser().getId() + " does not exist.");
        }

        if (!parkingSpotRepository.existsById(reservation.getParkingSpot().getId())) {
            throw new IllegalArgumentException("ParkingSpot with ID " + reservation.getParkingSpot().getId() + " does" +
                    " not exist.");
        }

        if (!parkingSpotRepository.getReferenceById(reservation.getParkingSpot().getId()).isActive()) {
            throw new IllegalArgumentException("ParkingSpot with ID " + reservation.getParkingSpot().getId() +
                    " is not active.");
        }

        if (!isParkingSpotFree(reservation)) {
            throw new ReservationConflictException("Conflict with existing reservation.");
        }

        ParkingSpot parkingSpot = parkingSpotRepository.getReferenceById(reservation.getParkingSpot().getId());

        Parking parking = parkingRepository.getReferenceById(parkingSpot.getParking().getId());
        OffsetDateTime startDate = reservation.getStartDate().withOffsetSameInstant(parking.getTimeZone());
        OffsetDateTime endDate = reservation.getEndDate().withOffsetSameInstant(parking.getTimeZone());

        if (!isParking24h(parking) && !isWithinParkingHours(startDate, endDate, parking)) {
            throw new IllegalArgumentException("Reservation not included in Parking's available hours.");
        }
        if (!checkTime(startDate, endDate)) {
            throw new IllegalArgumentException("Reservation invalid.");
        }

        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);

        Reservation createdReservation = reservationRepository.save(reservation);
        createdReservation.getParkingSpot().setParking(parking);
        return createdReservation;
    }

    public boolean checkTime(OffsetDateTime start, OffsetDateTime end) {
        OffsetDateTime currentDateTime = OffsetDateTime.now();
        OffsetDateTime adjustedStart = currentDateTime.withOffsetSameInstant(start.getOffset()).minusMinutes(15);

        boolean isAfterAdjustedStart = start.isAfter(adjustedStart);
        boolean isBeforeEnd = start.isBefore(end);

        return isAfterAdjustedStart && isBeforeEnd;
    }

    public boolean isParking24h(Parking parking) {
        OffsetTime parkingStart = parking.getStartTime();
        OffsetTime parkingEnd = parking.getEndTime();

        return parkingStart.equals(parkingEnd);
    }

    public boolean isWithinParkingHours(OffsetDateTime start, OffsetDateTime end, Parking parking) {
        OffsetTime parkingStart = parking.getStartTime();
        OffsetTime parkingEnd = parking.getEndTime();

        LocalTime startLocalTime = start.toLocalTime();
        LocalTime endLocalTime = end.toLocalTime();
        LocalTime parkingStartLocalTime = parkingStart.toLocalTime();
        LocalTime parkingEndLocalTime = parkingEnd.toLocalTime();

        boolean isStartAfterParkingStart = startLocalTime.isAfter(parkingStartLocalTime) || startLocalTime.equals(parkingStartLocalTime);
        boolean isStartBeforeParkingEnd = startLocalTime.isBefore(parkingEndLocalTime);
        boolean isEndAfterParkingStart = endLocalTime.isAfter(parkingStartLocalTime);
        boolean isEndBeforeParkingEnd = endLocalTime.isBefore(parkingEndLocalTime) || endLocalTime.equals(parkingEndLocalTime);

        return isStartAfterParkingStart
                && isStartBeforeParkingEnd
                && isEndAfterParkingStart
                && isEndBeforeParkingEnd;
    }



    public boolean isParkingSpotFree(Reservation reservation) {
        List<Reservation> existingReservations =
                reservationRepository.findByParkingSpotId(reservation.getParkingSpot().getId());
        for (Reservation existingReservation : existingReservations) {
            if (isDateRangeOverlap(existingReservation, reservation)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDateRangeOverlap(Reservation existingReservation, Reservation newReservation) {

        return newReservation.getStartDate().isBefore(existingReservation.getEndDate())
                && newReservation.getEndDate().isAfter(existingReservation.getStartDate());
    }

    public Reservation updateReservation(Reservation reservation) {
        if (!reservationRepository.existsById(reservation.getId())) {
            throw new IllegalArgumentException("Reservation with ID " + reservation.getId() + " does not exist.");
        }

        if (!userRepository.existsById(reservation.getUser().getId())) {
            throw new IllegalArgumentException("Client with ID " + reservation.getUser().getId() + " does not exist.");
        }

        if (!parkingSpotRepository.existsById(reservation.getParkingSpot().getId())) {
            throw new IllegalArgumentException("ParkingSpot with ID " + reservation.getParkingSpot().getId() + " does" +
                    " not exist.");
        }

        ParkingSpot parkingSpot = parkingSpotRepository.getReferenceById(reservation.getParkingSpot().getId());

        Parking parking = parkingRepository.getReferenceById(parkingSpot.getParking().getId());

        OffsetDateTime startDate = reservation.getStartDate().withOffsetSameInstant(parking.getTimeZone());
        OffsetDateTime endDate = reservation.getEndDate().withOffsetSameInstant(parking.getTimeZone());

        if (!isParking24h(parking) && !isWithinParkingHours(startDate, endDate, parking)) {
            throw new IllegalArgumentException("Reservation not included in Parking's available hours.");
        }
        if (!checkTime(startDate, endDate)) {
            throw new IllegalArgumentException("Reservation invalid.");
        }

        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setRegistrationNumber(reservation.getRegistrationNumber());
        reservation.setUser(reservation.getUser());
        reservation.setParkingSpot(reservation.getParkingSpot());
        reservation.setAmount(reservation.getAmount());

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public Reservation cancelReservation(Long id) {
        Optional<StripeCharge> stripeCharge = stripeChargeService.getStripeChargeByReservationId(id);
        if (stripeCharge.isPresent()) {
            StripeCharge refundedCharge = stripeChargeService.refundCharge(stripeCharge.get().getId());
            if (refundedCharge.getSuccess()) {
                refundedCharge.setReservation(null);
                StripeCharge updatedCharge = stripeChargeService.updateStripeCharge(refundedCharge);
                if (updatedCharge.getReservation() == null) {
                    Reservation canceledReservation = reservationRepository.getReferenceById(id);
                    deleteReservation(id);
                    return canceledReservation;
                }
            }
        }
        return null;
    }

    public void cancelReservationWithoutRefund(Long id) {
        Optional<StripeCharge> stripeCharge = stripeChargeService.getStripeChargeByReservationId(id);
        if(stripeCharge.isPresent()){
            stripeCharge.get().setReservation(null);
                reservationRepository.getReferenceById(id);
                deleteReservation(id);
        }
    }

    public Map<String, OffsetDateTime> getEarliestAvailableTime(ParkingSpot parkingSpot, OffsetDateTime date) {
        List<Reservation> reservations = reservationRepository.findByParkingSpotId(parkingSpot.getId())
                .stream()
                .filter(reservation -> reservation.getEndDate().isAfter(date))
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
        if(isParking24h(parkingSpot.getParking())){
            Map<String, OffsetDateTime> map = new HashMap<>();
            map.put("earliestStart", earliestAvailableTime);
            return map;
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
        List<Reservation> clientReservations = reservationRepository.findByUserId(client.getId());
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

    public List<Reservation> getAllReservationsByParkingId(Long id) {
        return reservationRepository.findAll()
                .stream()
                .filter(reservation -> Objects.equals(reservation.getParkingSpot().getParking().getId(), id))
                .sorted(Comparator.comparing(Reservation::getEndDate))
                .toList();
    }

    public List<Reservation> getAllReservationsByParkingSpotId(Long id) {
        return reservationRepository.findAll()
                .stream()
                .filter(reservation -> Objects.equals(reservation.getParkingSpot().getId(), id))
                .sorted(Comparator.comparing(Reservation::getEndDate))
                .toList();
    }

    public List<Reservation> getFutureReservationByParkingSpotId(Long id) {
        return reservationRepository.findAll()
                .stream()
                .filter(reservation -> Objects.equals(reservation.getParkingSpot().getId(), id))
                .filter(reservation -> reservation.getStartDate().isAfter(OffsetDateTime.now()))
                .sorted(Comparator.comparing(Reservation::getEndDate))
                .toList();
    }
}
