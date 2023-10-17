package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.*;
import net.parkvision.parkvisionbackend.exception.ReservationConflictException;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.service.ReservationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService _reservationService;

    private final ModelMapper modelMapper;

    @Autowired
    public ReservationController(ReservationService reservationService, ModelMapper modelMapper) {
        _reservationService = reservationService;
        this.modelMapper = modelMapper;
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


    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = _reservationService.getAllReservations().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = _reservationService.getReservationById(id);
        return reservation.map(value -> ResponseEntity.ok(convertToDto(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //todo znajdz rezerwacje danego parkingu po id
    //todo znajdz rezerwacje danego parkingu po id i dacie

    @PreAuthorize("hasAnyRole('USER','PARKING_MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationDTO reservationDto) throws ReservationConflictException {
        Reservation createdReservation = _reservationService.createReservation(convertToEntity(reservationDto));
        return ResponseEntity.ok(convertToDto(createdReservation));
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER', 'ADMIN')")
    @PutMapping
    public ResponseEntity<ReservationDTO> updateReservation(@RequestBody ReservationDTO reservationDto) {
        try {
            Reservation updatedReservation = _reservationService.updateReservation(convertToEntity(reservationDto));
            return ResponseEntity.ok(convertToDto(updatedReservation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        _reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
