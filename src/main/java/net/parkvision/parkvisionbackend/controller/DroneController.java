package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.DroneDTO;
import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.service.DroneService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drones")
public class DroneController {

    private final DroneService droneService;

    private final ModelMapper modelMapper;

    @Autowired
    public DroneController(DroneService droneService, ModelMapper modelMapper) {
        this.droneService = droneService;
        this.modelMapper = modelMapper;
    }

    private DroneDTO convertToDTO(Drone drone) {
        DroneDTO droneDTO = modelMapper.map(drone, DroneDTO.class);
        droneDTO.setParkingDTO(modelMapper.map(drone.getParking(), ParkingDTO.class));
        return droneDTO;
    }

    private Drone convertToEntity(DroneDTO droneDTO) {
        return modelMapper.map(droneDTO, Drone.class);
    }

    @GetMapping
    public ResponseEntity<List<DroneDTO>> getAllDrones() {
        List<DroneDTO> drones = droneService.getAllDrones().stream().map(
                this::convertToDTO
        ).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(drones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DroneDTO> getDroneById(@PathVariable Long id) {
        Optional<Drone> drone = droneService.getDroneById(id);
        if (drone.isPresent()) {
            return ResponseEntity.ok(convertToDTO(drone.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<DroneDTO> createDrone(@RequestBody DroneDTO droneDto) {
        Drone createdDrone = droneService.createDrone(convertToEntity(droneDto));
        return ResponseEntity.ok(convertToDTO(createdDrone));
    }

    @PutMapping()
    public ResponseEntity<DroneDTO> updateDrone(@RequestBody DroneDTO droneDto) {
        try {
            Drone updatedDrone = droneService.updateDrone(convertToEntity(droneDto));
            return ResponseEntity.ok(convertToDTO(updatedDrone));
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
