package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parkings")
public class ParkingController {

    private final ParkingService _parkingService;

    @Autowired
    public ParkingController(ParkingService parkingService) {
        _parkingService = parkingService;
    }

    @GetMapping
    public ResponseEntity<List<Parking>> getAllParkings() {
        List<Parking> parkings = _parkingService.getAllParkings();
        return ResponseEntity.ok(parkings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parking> getParkingById(@PathVariable Long id) {
        Optional<Parking> parking = _parkingService.getParkingById(id);
        return parking.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Parking> createParking(@RequestBody Parking parking) {
        Parking createdParking = _parkingService.createParking(parking);
        return ResponseEntity.ok(createdParking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Parking> updateParking(@PathVariable Long id, @RequestBody Parking parking) {
        try {
            Parking updatedParking = _parkingService.updateParking(id, parking);
            return ResponseEntity.ok(updatedParking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParking(@PathVariable Long id) {
        _parkingService.deleteParking(id);
        return ResponseEntity.noContent().build();
    }
}
