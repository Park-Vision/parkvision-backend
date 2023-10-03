package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.DroneMissionDTO;
import net.parkvision.parkvisionbackend.model.DroneMission;
import net.parkvision.parkvisionbackend.service.DroneMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drone-missions")
public class DroneMissionController {

    private final DroneMissionService _droneMissionService;

    @Autowired
    public DroneMissionController(DroneMissionService droneMissionService) {
        _droneMissionService = droneMissionService;
    }

    @GetMapping
    public ResponseEntity<List<DroneMission>> getAllDroneMissions() {
        List<DroneMission> droneMissions = _droneMissionService.getAllDroneMissions();
        return ResponseEntity.ok(droneMissions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DroneMission> getDroneMissionById(@PathVariable Long id) {
        Optional<DroneMission> droneMission = _droneMissionService.getDroneMissionById(id);
        return droneMission.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DroneMission> createDroneMission(DroneMissionDTO droneMissionDto) {
        DroneMission createdDroneMission = _droneMissionService.createDroneMission(droneMissionDto);
        return ResponseEntity.ok(createdDroneMission);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DroneMission> updateDroneMission(@PathVariable Long id, DroneMissionDTO droneMissionDto) {
        try {
            DroneMission updatedDroneMission = _droneMissionService.updateDroneMission(id, droneMissionDto);
            return ResponseEntity.ok(updatedDroneMission);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDroneMission(@PathVariable Long id) {
        _droneMissionService.deleteDroneMission(id);
        return ResponseEntity.noContent().build();
    }

}
