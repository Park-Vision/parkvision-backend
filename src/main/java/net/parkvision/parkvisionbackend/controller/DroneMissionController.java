package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.*;
import net.parkvision.parkvisionbackend.model.DroneMission;
import net.parkvision.parkvisionbackend.model.MissionSpotResult;
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

    private final DroneMissionService droneMissionService;
    private final ModelMapper modelMapper;

    @Autowired
    public DroneMissionController(DroneMissionService droneMissionService, ModelMapper modelMapper) {
        this.droneMissionService = droneMissionService;
        this.modelMapper = modelMapper;
    }

    public MissionSpotResultDTO convertToDtoMissionSpot(MissionSpotResult missionSpotResult) {
        MissionSpotResultDTO missionSpotResultDTO = modelMapper.map(missionSpotResult, MissionSpotResultDTO.class);
        missionSpotResultDTO.setParkingSpotDTO(modelMapper.map(missionSpotResult.getParkingSpot(),
                ParkingSpotDTO.class));
        return missionSpotResultDTO;
    }

    private DroneMissionDTO convertToDTO(DroneMission droneMission) {
        DroneMissionDTO droneMissionDTO = modelMapper.map(droneMission, DroneMissionDTO.class);
        droneMissionDTO.setDroneDTO(modelMapper.map(droneMission.getDrone(), DroneDTO.class));
        droneMissionDTO.setParkingDTO(modelMapper.map(droneMission.getParking(), ParkingDTO.class));
        if (!droneMission.getMissionSpotResultList().isEmpty()) {
            droneMissionDTO.setMissionSpotResultList(droneMission.getMissionSpotResultList().stream().map(
                    this::convertToDtoMissionSpot
            ).collect(Collectors.toList()));
        }
        return droneMissionDTO;
    }

    private DroneMission convertToEntity(DroneMissionDTO droneMissionDTO) {
        return modelMapper.map(droneMissionDTO, DroneMission.class);
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @GetMapping
    public ResponseEntity<List<DroneMissionDTO>> getAllDroneMissions() {
        List<DroneMissionDTO> droneMissions
                = droneMissionService.getAllDroneMissions().stream().map(
                this::convertToDTO
        ).collect(Collectors.toList());
        return ResponseEntity.ok(droneMissions);
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<DroneMissionDTO> getDroneMissionById(@PathVariable Long id) {
        Optional<DroneMission> droneMission = droneMissionService.getDroneMissionById(id);
        return droneMission.map(mission -> ResponseEntity.ok(convertToDTO(mission))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping
    public ResponseEntity<DroneMissionDTO> createDroneMission(@RequestBody DroneMissionDTO droneMissionDto) {
        DroneMission createdDroneMission = droneMissionService.createDroneMission(convertToEntity(droneMissionDto));
        return ResponseEntity.ok(convertToDTO(createdDroneMission));
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PutMapping
    public ResponseEntity<DroneMissionDTO> updateDroneMission(@RequestBody DroneMissionDTO droneMissionDto) {
        try {
            DroneMission updatedDroneMission =
                    droneMissionService.updateDroneMission(convertToEntity(droneMissionDto));
            return ResponseEntity.ok(convertToDTO(updatedDroneMission));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDroneMission(@PathVariable Long id) {
        droneMissionService.deleteDroneMission(id);
        return ResponseEntity.noContent().build();
    }
}
