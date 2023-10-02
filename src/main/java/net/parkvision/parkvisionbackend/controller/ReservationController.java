package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping ("/api/reservations")
public class ReservationController {
    private final ReservationService _reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        _reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = _reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = _reservationService.getReservationById(id);
        return reservation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //todo znajdz rezerwacje danego parkingu po id
    //todo znajdz rezerwacje danego parkingu po id i dacie

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        Reservation createdReservation = _reservationService.createReservation(reservation);
        return ResponseEntity.ok(createdReservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        try {
            Reservation updatedReservation = _reservationService.updateReservation(id, reservation);
            return ResponseEntity.ok(updatedReservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        _reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
