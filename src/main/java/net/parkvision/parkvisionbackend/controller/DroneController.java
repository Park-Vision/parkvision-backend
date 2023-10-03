package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.DroneDTO;
import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.service.DroneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drones")
public class DroneController {

    private final DroneService droneService;

    @Autowired
    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    @GetMapping
    public ResponseEntity<List<Drone>> getAllDrones() {
        List<Drone> drones = droneService.getAllDrones();
        return ResponseEntity.ok(drones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Drone> getDroneById(@PathVariable Long id) {
        Optional<Drone> drone = droneService.getDroneById(id);
        return drone.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Drone> createDrone(@RequestBody DroneDTO droneDto) {
        Drone createdDrone = droneService.createDrone(droneDto);
        return ResponseEntity.ok(createdDrone);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Drone> updateDrone(@PathVariable Long id, @RequestBody DroneDTO droneDto) {
        try {
            Drone updatedDrone = droneService.updateDrone(id, droneDto);
            return ResponseEntity.ok(updatedDrone);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrone(@PathVariable Long id) {
        droneService.deleteDrone(id);
        return ResponseEntity.noContent().build();
    }
}
