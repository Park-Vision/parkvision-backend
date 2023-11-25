package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.*;
import net.parkvision.parkvisionbackend.exception.ReservationConflictException;
import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final ReservationService reservationService;

    private final ParkingSpotService parkingSpotService;
    private final ParkingService parkingService;
    private final EmailSenderService emailSenderService;
    private final ModelMapper modelMapper;
    @Value("${park-vision.hour-rule}")
    private int hourRule;

    @Autowired
    public ReservationController(ReservationService reservationService,
                                 EmailSenderService emailSenderService,
                                 ModelMapper modelMapper,
                                 ParkingSpotService parkingSpotService, ParkingService parkingService) {
        this.reservationService = reservationService;
        this.modelMapper = modelMapper;
        this.emailSenderService = emailSenderService;
        this.parkingSpotService = parkingSpotService;
        this.parkingService = parkingService;
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
        List<ReservationDTO> reservations = reservationService.getAllReservations().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
        return ResponseEntity.ok(reservations);
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        return reservation.map(value -> ResponseEntity.ok(convertToDto(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //todo znajdz rezerwacje danego parkingu po id i dacie

    @PreAuthorize("hasAnyRole('USER','PARKING_MANAGER')")
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationDTO reservationDto) throws ReservationConflictException {
        Optional<ParkingSpot> parkingSpot =
                parkingSpotService.getParkingSpotById(reservationDto.getParkingSpotDTO().getId());
        if (parkingSpot.isPresent()) {
            Reservation createdReservation = reservationService.createReservation(convertToEntity(reservationDto));
            return ResponseEntity.ok(convertToDto(createdReservation));
        }
        return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @PutMapping
    public ResponseEntity<ReservationDTO> updateReservation(@RequestBody ReservationDTO reservationDto) {
        User user = RequestContext.getUserFromRequest();
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getRole().equals(Role.USER)) {
            Optional<Reservation> reservation = reservationService.getReservationById(reservationDto.getId());

            if (reservation.isPresent() && !reservation.get().getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().build();
            }
        }
        try {

            Reservation updatedReservation = reservationService.updateReservation(convertToEntity(reservationDto));
            Optional<ParkingSpot> parkingSpot =
                    parkingSpotService.getParkingSpotById(reservationDto.getParkingSpotDTO().getId());
            if (parkingSpot.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (updatedReservation.getUser().getId().equals(user.getId())
                    || user.getRole().equals(Role.PARKING_MANAGER)) {
                try {
                    emailSenderService.sendHtmlEmailReservation(
                            updatedReservation.getUser().getFirstname(),
                            updatedReservation.getUser().getLastname(),
                            updatedReservation.getUser().getEmail(),
                            "Reservation update confirmation",
                            "Here is the confirmation of the reservation update in our system. ",
                            parkingSpot.get().getParking(),
                            updatedReservation, "ParkVision reservation update confirmation");
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        Optional<Reservation> reservation = reservationService.getReservationById(id);


        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        OffsetDateTime now = OffsetDateTime.now();


        if (reservation.isPresent() && user.getRole().equals(Role.PARKING_MANAGER)) {
            ParkingManager parkingManager = (ParkingManager) user;

            if (!reservation.get().getParkingSpot().getParking().getId().equals(parkingManager.getParking().getId())) {
                return ResponseEntity.badRequest().body("Parking manager does not have permission to delete this " +
                        "reservation.");
            }

            if (reservation.get().getStartDate().isBefore(now)) {
                return ResponseEntity.badRequest().body("Cannot cancel reservation in the past.");
            }

            if (reservation.get().getUser().getId().equals(user.getId())) {
                reservationService.deleteReservation(id);
            } else {
                if (reservation.get().getStartDate().isBefore(now.plusHours(hourRule))) {
                    reservationService.deleteReservation(id);
                } else {
                    reservationService.cancelReservation(id);
                }
                try {
                    emailSenderService.sendHtmlEmailReservation(
                            reservation.get().getUser().getFirstname(),
                            reservation.get().getUser().getLastname(),
                            reservation.get().getUser().getEmail(),
                            "Reservation cancellation confirmation",
                            "Parking Manager canceled your reservation. ",
                            reservation.get().getParkingSpot().getParking(),
                            reservation.get(), "ParkVision reservation cancellation confirmation");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ResponseEntity.ok().build();
        } else if (reservation.isPresent() && user.getRole().equals(Role.USER)) {
            if (!reservation.get().getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("User does not have permission to cancel this reservation.");
            }

            if (reservation.get().getStartDate().isBefore(now.plusHours(hourRule))) {
                return ResponseEntity.badRequest().body("Cannot cancel reservation less than " + hourRule + " hours before start.");
            }

            Reservation canceledReservation = reservationService.cancelReservation(id);
            if (canceledReservation == null) {
                return ResponseEntity.badRequest().body("Failed to cancel the reservation.");
            }
            Optional<ParkingSpot> parkingSpot =
                    parkingSpotService.getParkingSpotById(canceledReservation.getParkingSpot().getId());
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

        Map<String, List<Reservation>> clientReservations = reservationService.getSortedReservationsByClient(client);

        Map<String, List<ReservationDTO>> clientReservationsResponse = new HashMap<>();

        for (String category : clientReservations.keySet()) {
            clientReservationsResponse.put(category, clientReservations.get(category).stream()
                    .map(this::convertToDto)
                    .toList());
        }

        return ResponseEntity.ok(clientReservationsResponse);
    }

    @PreAuthorize("hasRole('PARKING_MANAGER')")
    @GetMapping("/parking/{id}")
    public ResponseEntity<List<ReservationDTO>> getAllReservationsByParking(@PathVariable Long id) {

        User user = RequestContext.getUserFromRequest();
        Optional<Parking> parking = parkingService.getParkingById(id);
        if (user != null && user.getRole().equals(Role.PARKING_MANAGER)) {
            if (parking.isPresent()){
                ParkingManager parkingManager = (ParkingManager) user;
                if((parkingManager.getParking().getId().equals(parking.get().getId()))){
                    List<ReservationDTO> reservations = reservationService.getAllReservationsByParkingId(id).stream().map(
                            this::convertToDto
                    ).collect(Collectors.toList());
                    return ResponseEntity.ok(reservations);
                }
                else{
                    return ResponseEntity.status(401).build();
                }
            }
            else{
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
