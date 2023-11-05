package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.DroneDTO;
import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.kafka.KafkaTopicConfig;
import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.model.ParkingModerator;
import net.parkvision.parkvisionbackend.service.DroneService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/drones")
public class DroneController {

    private final DroneService droneService;

    private final ModelMapper modelMapper;

    private final KafkaTemplate<String, String> kafkaTemplate;

    //@Autowired
    //KafkaTopicConfig kafkaTopicConfig;

    @Autowired
    public DroneController(DroneService droneService, ModelMapper modelMapper,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.droneService = droneService;
        this.modelMapper = modelMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping("/{id}/{command}")
    public ResponseEntity<DroneDTO> startDrone(@PathVariable Long id, @PathVariable String command) {
        Optional<Drone> drone = droneService.getDroneById(id);
        if (drone.isPresent()) {
            System.out.println("drone-" + id);
            // PRODUCTION
            //kafkaTemplate.send("drone-" + id, command);
            // TEST WS
            //kafkaTemplate.send("drones-info", String.valueOf(id), command);
            return ResponseEntity.ok(convertToDTO(drone.get()));
        }
        return ResponseEntity.notFound().build();
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

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @GetMapping("/parking/{id}")
    public ResponseEntity<List<DroneDTO>> getAllDronesByParkingId(@PathVariable Long id) {
        ParkingModerator parkingModerator = getParkingModeratorFromRequest();
        if (!Objects.equals(parkingModerator.getParking().getId(), id)) {
            return ResponseEntity.badRequest().build();
        }
        List<DroneDTO> drones = droneService.getAllDronesByParkingId(id).stream().map(
                this::convertToDTO
        ).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(drones);
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<DroneDTO> getDroneById(@PathVariable Long id) {
        ParkingModerator parkingModerator = getParkingModeratorFromRequest();
        if (parkingModerator == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Drone> drone = droneService.getDroneById(id);
        if (drone.isPresent()) {
            if (!Objects.equals(parkingModerator.getParking().getId(), drone.get().getParking().getId())) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(convertToDTO(drone.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping
    public ResponseEntity<DroneDTO> createDrone(@RequestBody DroneDTO droneDto) throws ExecutionException,
            InterruptedException {
        ParkingModerator parkingModerator = getParkingModeratorFromRequest();
        if (!Objects.equals(parkingModerator.getParking().getId(), droneDto.getParkingDTO().getId())) {
            return ResponseEntity.badRequest().build();
        }

        Drone createdDrone = droneService.createDrone(convertToEntity(droneDto));

        try {
            //kafkaTopicConfig.createNewTopic("drone-" + createdDrone.getId());
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok(convertToDTO(createdDrone));
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PutMapping()
    public ResponseEntity<DroneDTO> updateDrone(@RequestBody DroneDTO droneDto) {
        // TODO: can parking manager change parking of a drone?? - noooo!
        try {
            Drone updatedDrone = droneService.updateDrone(convertToEntity(droneDto));
            return ResponseEntity.ok(convertToDTO(updatedDrone));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrone(@PathVariable Long id) {
        ParkingModerator parkingModerator = getParkingModeratorFromRequest();
        if (parkingModerator == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Drone> drone = droneService.getDroneById(id);
        if (!Objects.equals(parkingModerator.getParking().getId(), drone.get().getParking().getId())) {
            return ResponseEntity.badRequest().build();
        }


        droneService.deleteDrone(id);
        return ResponseEntity.noContent().build();
    }

    private ParkingModerator getParkingModeratorFromRequest() {
        Object user = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if (user instanceof ParkingModerator) {
            return (ParkingModerator) user;
        }
        return null;
    }
}
