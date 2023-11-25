package net.parkvision.parkvisionbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.parkvision.parkvisionbackend.config.MessageEncryptor;
import net.parkvision.parkvisionbackend.dto.DroneDTO;
import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.dto.ParkingSpotCoordinatesDTO;
import net.parkvision.parkvisionbackend.kafka.KafkaTopicConfig;
import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.model.ParkingManager;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.service.DroneService;
import net.parkvision.parkvisionbackend.service.RequestContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/drones")
public class DroneController {

    private final DroneService droneService;

    private final ModelMapper modelMapper;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    KafkaTopicConfig kafkaTopicConfig;
    private final ParkingSpotController parkingSpotController;

    @Autowired
    public DroneController(DroneService droneService, ModelMapper modelMapper,
                           KafkaTemplate<String, String> kafkaTemplate, ParkingSpotController parkingSpotController) {
        this.droneService = droneService;
        this.modelMapper = modelMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.parkingSpotController = parkingSpotController;
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping("/{id}/{command}")
    public ResponseEntity<DroneDTO> startDrone(@PathVariable Long id, @PathVariable String command) {
        User user = RequestContext.getUserFromRequest();
        Optional<Drone> drone = droneService.getDroneById(id);
        ParkingManager parkingManager = (ParkingManager) user;
        if (drone.isPresent()) {
            assert parkingManager != null;
            if (Objects.equals(parkingManager.getParking().getId(), drone.get().getParking().getId())) {
                System.out.println("drone-" + id);
                // PRODUCTION
                //kafkaTemplate.send("drone-" + id, command);
                // TEST WS
                try {
                    String encrypt;
                    if (command.equals("start") || command.equals("stop")) {
                        List<ParkingSpotCoordinatesDTO> parkingSpotCoordinatesDTOList =
                                parkingSpotController.getSpotCoordinatesByDroneId(id);
                        Map<String, Object> map = new HashMap<>();
                        map.put("command", command);
                        map.put("cords", parkingSpotCoordinatesDTOList);
                        ObjectMapper objectMapper = new ObjectMapper();

                        String json = objectMapper.writeValueAsString(map);
                        System.out.println(json);
                        encrypt = MessageEncryptor.encryptMessage(json, drone.get().getDroneKey());
                        System.out.println(encrypt);
                        kafkaTemplate.send("drones-info", String.valueOf(id), encrypt);
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return ResponseEntity.ok(convertToDTO(drone.get()));
            }
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
        ParkingManager parkingManager = getParkingManagerFromRequest();
        assert parkingManager != null;
        if (!Objects.equals(parkingManager.getParking().getId(), id)) {
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
        ParkingManager parkingManager = getParkingManagerFromRequest();
        if (parkingManager == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Drone> drone = droneService.getDroneById(id);
        if (drone.isPresent()) {
            if (!Objects.equals(parkingManager.getParking().getId(), drone.get().getParking().getId())) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(convertToDTO(drone.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping
    public ResponseEntity<DroneDTO> createDrone(@RequestBody DroneDTO droneDto) {
        ParkingManager parkingManager = getParkingManagerFromRequest();
        assert parkingManager != null;
        if (!Objects.equals(parkingManager.getParking().getId(), droneDto.getParkingDTO().getId())) {
            return ResponseEntity.badRequest().build();
        }

        Drone createdDrone = droneService.createDrone(convertToEntity(droneDto));

        try {
            kafkaTopicConfig.createNewTopic("drone-" + createdDrone.getId());
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok(convertToDTO(createdDrone));
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PutMapping()
    public ResponseEntity<DroneDTO> updateDrone(@RequestBody DroneDTO droneDto) {
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
        ParkingManager parkingManager = getParkingManagerFromRequest();
        if (parkingManager == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Drone> drone = droneService.getDroneById(id);
        if(drone.isPresent()){
            if (!Objects.equals(parkingManager.getParking().getId(), drone.get().getParking().getId())) {
                return ResponseEntity.badRequest().build();
            }
            droneService.deleteDrone(id);
        }
        return ResponseEntity.noContent().build();
    }

    private ParkingManager getParkingManagerFromRequest() {
        User user = RequestContext.getUserFromRequest();
        if (user instanceof ParkingManager) {
            return (ParkingManager) user;
        }
        return null;
    }
}
