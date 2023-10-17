package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.DroneDTO;
import net.parkvision.parkvisionbackend.dto.DroneMissionDTO;
import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.model.DroneMission;
import net.parkvision.parkvisionbackend.service.DroneMissionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drone-missions")
public class DroneMissionController {

    private final DroneMissionService _droneMissionService;

    private final ModelMapper modelMapper;

    @Autowired
    public DroneMissionController(DroneMissionService droneMissionService, ModelMapper modelMapper) {
        _droneMissionService = droneMissionService;
        this.modelMapper = modelMapper;
    }

    private DroneMissionDTO convertToDTO(DroneMission droneMission) {
        DroneMissionDTO droneMissionDTO = modelMapper.map(droneMission, DroneMissionDTO.class);
        droneMissionDTO.setDroneDTO(modelMapper.map(droneMission.getDrone(), DroneDTO.class));
        droneMissionDTO.setParkingDTO(modelMapper.map(droneMission.getParking(), ParkingDTO.class));
        return droneMissionDTO;
    }

    private DroneMission convertToEntity(DroneMissionDTO droneMissionDTO) {
        return modelMapper.map(droneMissionDTO, DroneMission.class);
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @GetMapping
    public ResponseEntity<List<DroneMissionDTO>> getAllDroneMissions() {
        List<DroneMissionDTO> droneMissions
                = _droneMissionService.getAllDroneMissions().stream().map(
                this::convertToDTO
        ).collect(Collectors.toList());
        return ResponseEntity.ok(droneMissions);
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<DroneMissionDTO> getDroneMissionById(@PathVariable Long id) {
        Optional<DroneMission> droneMission = _droneMissionService.getDroneMissionById(id);
        if (droneMission.isPresent()) {
            return ResponseEntity.ok(convertToDTO(droneMission.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping
    public ResponseEntity<DroneMissionDTO> createDroneMission(@RequestBody DroneMissionDTO droneMissionDto) {
        DroneMission createdDroneMission = _droneMissionService.createDroneMission(convertToEntity(droneMissionDto));
        return ResponseEntity.ok(convertToDTO(createdDroneMission));
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PutMapping
    public ResponseEntity<DroneMissionDTO> updateDroneMission(@RequestBody DroneMissionDTO droneMissionDto) {
        try {
            DroneMission updatedDroneMission =
                    _droneMissionService.updateDroneMission(convertToEntity(droneMissionDto));
            return ResponseEntity.ok(convertToDTO(updatedDroneMission));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDroneMission(@PathVariable Long id) {
        _droneMissionService.deleteDroneMission(id);
        return ResponseEntity.noContent().build();
    }
}
