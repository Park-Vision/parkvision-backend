package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.*;
import net.parkvision.parkvisionbackend.exception.ReservationConflictException;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.service.EmailSenderService;
import net.parkvision.parkvisionbackend.service.ParkingSpotService;
import net.parkvision.parkvisionbackend.service.ReservationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            User user = getUserFromRequest();
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            try {
                emailSenderService.sendHtmlEmailReservationCreated(
                        user.getFirstname(),
                        user.getLastname(),
                        user.getEmail(),
                        parkingSpot.get().getParking(),
                        createdReservation, "ParkVision reservation confirmation");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("return ok");
        return ResponseEntity.ok(convertToDto(createdReservation));
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @PutMapping
    public ResponseEntity<ReservationDTO> updateReservation(@RequestBody ReservationDTO reservationDto) {
        try {
            Reservation updatedReservation = _reservationService.updateReservation(convertToEntity(reservationDto));
            return ResponseEntity.ok(convertToDto(updatedReservation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        _reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    private User getUserFromRequest(){
        Object user = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(user instanceof User) {
            return (User) user;
        }
        return null;
    }
}
