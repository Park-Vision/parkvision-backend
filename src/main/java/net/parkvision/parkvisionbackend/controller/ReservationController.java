package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.*;
import net.parkvision.parkvisionbackend.exception.ReservationConflictException;
import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.service.EmailSenderService;
import net.parkvision.parkvisionbackend.service.ParkingSpotService;
import net.parkvision.parkvisionbackend.service.RequestContext;
import net.parkvision.parkvisionbackend.service.ReservationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService _reservationService;

    private final ParkingSpotService _parkingSpotService;
    private final EmailSenderService emailSenderService;
    private final ModelMapper modelMapper;

    @Autowired
    public ReservationController(ReservationService reservationService,
                                 EmailSenderService emailSenderService,
                                 ModelMapper modelMapper,
                                 ParkingSpotService parkingSpotService) {
        _reservationService = reservationService;
        this.modelMapper = modelMapper;
        this.emailSenderService = emailSenderService;
        _parkingSpotService = parkingSpotService;
    }

    private ReservationDTO convertToDto(Reservation reservation) {
        ReservationDTO reservationDTO = modelMapper.map(reservation, ReservationDTO.class);
        reservationDTO.setUserDTO(modelMapper.map(reservation.getUser(), UserDTO.class));
        reservationDTO.setParkingSpotDTO(modelMapper.map(reservation.getParkingSpot(), ParkingSpotDTO.class));
        reservationDTO.getParkingSpotDTO().setParkingDTO(modelMapper.map(reservation.getParkingSpot().getParking(),
                ParkingDTO.class));
        return reservationDTO;
    }

    private Reservation convertToEntity(ReservationDTO reservationDTO) {

        return modelMapper.map(reservationDTO, Reservation.class);
    }


    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = _reservationService.getAllReservations().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
        return ResponseEntity.ok(reservations);
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = _reservationService.getReservationById(id);
        return reservation.map(value -> ResponseEntity.ok(convertToDto(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //todo znajdz rezerwacje danego parkingu po id
    //todo znajdz rezerwacje danego parkingu po id i dacie

    @PreAuthorize("hasAnyRole('USER','PARKING_MANAGER')")
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationDTO reservationDto) throws ReservationConflictException {
        Reservation createdReservation = _reservationService.createReservation(convertToEntity(reservationDto));
        Optional<ParkingSpot> parkingSpot = _parkingSpotService.getParkingSpotById(createdReservation.getParkingSpot().getId());
        if (parkingSpot.isPresent()) {
            User user = RequestContext.getUserFromRequest();
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            if(user.getRole().equals(Role.USER)) {
                try {
                    emailSenderService.sendHtmlEmailReservation(
                            user.getFirstname(),
                            user.getLastname(),
                            user.getEmail(),
                        "Reservation confirmation",
                        "Here is the confirmation of the reservation you made in our system. ",
                            parkingSpot.get().getParking(),
                            createdReservation, "ParkVision reservation confirmation");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("return ok");
        return ResponseEntity.ok(convertToDto(createdReservation));
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @PutMapping
    public ResponseEntity<ReservationDTO> updateReservation(@RequestBody ReservationDTO reservationDto) {
        User user = RequestContext.getUserFromRequest();
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getRole().equals(Role.USER)) {
            Optional<Reservation> reservation = _reservationService.getReservationById(reservationDto.getId());

            if (reservation.isPresent() && !reservation.get().getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().build();
            }
        }
        try {

            Reservation updatedReservation = _reservationService.updateReservation(convertToEntity(reservationDto));
            Optional<ParkingSpot> parkingSpot = _parkingSpotService.getParkingSpotById(reservationDto.getParkingSpotDTO().getId());
            if (parkingSpot.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            try {
                emailSenderService.sendHtmlEmailReservation(
                        user.getFirstname(),
                        user.getLastname(),
                        user.getEmail(),
                        "Reservation change confirmation",
                        "Here is the confirmation of the reservation change you made in our system. ",
                        parkingSpot.get().getParking(),
                        updatedReservation, "ParkVision reservation change confirmation");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok(convertToDto(updatedReservation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long id) {
        User user = RequestContext.getUserFromRequest();
        Optional<Reservation> reservation = _reservationService.getReservationById(id);


        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        if (reservation.isPresent()) {
            OffsetDateTime reservationStartDate = reservation.get().getStartDate();
            OffsetDateTime now = OffsetDateTime.now();
            if (reservationStartDate.isBefore(now)) {
                return ResponseEntity.badRequest().body("Cannot cancel reservation in the past.");
            }
        }
        if (reservation.isPresent() && user.getRole().equals(Role.PARKING_MANAGER)) {
            ParkingModerator parkingManager = (ParkingModerator) user;

            if (!reservation.get().getParkingSpot().getParking().getId().equals(parkingManager.getParking().getId())) {
                return ResponseEntity.badRequest().body("Parking manager does not have permission to delete this reservation.");
            }

            if(reservation.get().getUser().getId().equals(user.getId())){
                _reservationService.deleteReservation(id);
                return ResponseEntity.ok().build();
            } else {
                try {
                    emailSenderService.sendHtmlEmailReservation(
                            reservation.get().getUser().getFirstname(),
                            reservation.get().getUser().getLastname(),
                            reservation.get().getUser().getEmail(),
                            "Reservation cancellation confirmation",
                            "Here is the confirmation of the reservation cancellation you made in our system. ",
                            reservation.get().getParkingSpot().getParking(),
                            reservation.get(), "ParkVision reservation cancellation confirmation");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                _reservationService.deleteReservation(id);
                return ResponseEntity.ok().build();
            }
        } else if (reservation.isPresent() && user.getRole().equals(Role.USER)) {
            if (!reservation.get().getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("User does not have permission to cancel this reservation.");
            }
            Reservation canceledReservation = _reservationService.cancelReservation(id);
            if (canceledReservation == null) {
                return ResponseEntity.badRequest().body("Failed to cancel the reservation.");
            }
            Optional<ParkingSpot> parkingSpot = _parkingSpotService.getParkingSpotById(canceledReservation.getParkingSpot().getId());
            if (parkingSpot.isEmpty()) {
                return ResponseEntity.badRequest().body("Failed to retrieve parking spot information.");
            }
            try {
                emailSenderService.sendHtmlEmailReservation(
                        user.getFirstname(),
                        user.getLastname(),
                        user.getEmail(),
                        "Reservation cancellation confirmation",
                        "Here is the confirmation of the reservation cancellation you made in our system. ",
                        parkingSpot.get().getParking(),
                        canceledReservation, "ParkVision reservation cancellation confirmation");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @GetMapping("/client")
    public ResponseEntity<Map<String, List<ReservationDTO>>> getClientReservations() {

        User client = RequestContext.getUserFromRequest();
        if (client == null) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, List<Reservation>> clientReservations = _reservationService.getSortedReservationsByClient(client);

        Map<String, List<ReservationDTO>> clientReservationsResponse = new HashMap<>();

        for (String category : clientReservations.keySet()) {
            clientReservationsResponse.put(category, clientReservations.get(category).stream()
                    .map(this::convertToDto)
                    .toList());
        }

        return ResponseEntity.ok(clientReservationsResponse);
    }
}
