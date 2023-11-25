package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.service.ParkingService;
import net.parkvision.parkvisionbackend.service.ParkingSpotService;
import net.parkvision.parkvisionbackend.service.RequestContext;
import net.parkvision.parkvisionbackend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parkings")
public class ParkingController {

    private final ParkingService parkingService;
    private final ParkingSpotService parkingSpotService;

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public ParkingController(ParkingService parkingService, ParkingSpotService parkingSpotService,
                             ModelMapper modelMapper, UserService userService) {
        this.parkingService = parkingService;
        this.parkingSpotService = parkingSpotService;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    private ParkingDTO convertToDTO(Parking parking) {
        return modelMapper.map(parking, ParkingDTO.class);
    }

    private Parking convertToEntity(ParkingDTO parkingDTO) {
        return modelMapper.map(parkingDTO, Parking.class);
    }


    @GetMapping
    public ResponseEntity<List<ParkingDTO>> getAllParkings() {
        List<ParkingDTO> parkings
                = parkingService.getAllParkings().stream().map(
                this::convertToDTO
        ).collect(Collectors.toList());
        User user = RequestContext.getUserFromRequest();
        if (user == null || user.getRole().equals(Role.USER)) {
            return ResponseEntity.ok(parkings);
        }
        List<ParkingDTO> filteredParking =
                parkings.stream()
                        .filter(parkingDTO ->
                                parkingDTO.getId().equals(((ParkingManager) user).getParking().getId())).toList();
        return ResponseEntity.ok(filteredParking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingDTO> getParkingById(@PathVariable Long id) {
        User user = RequestContext.getUserFromRequest();
        Optional<Parking> parking = parkingService.getParkingById(id);

        if (user == null || user.getRole().equals(Role.USER)) {
            return parking.map(value -> ResponseEntity.ok(convertToDTO(value))).orElseGet(() -> ResponseEntity.notFound().build());
        } else if (user.getRole().equals(Role.PARKING_MANAGER)) {
            if (parking.isPresent()) {
                ParkingManager parkingManager = (ParkingManager) user;
                if ((parkingManager.getParking().getId().equals(parking.get().getId()))) {
                    return ResponseEntity.ok(convertToDTO(parking.get()));
                } else {
                    return ResponseEntity.status(401).build();
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/spots-number")
    public ResponseEntity<Integer> getParkingSpotsNumber(@PathVariable Long id) {
        Optional<Parking> parking = parkingService.getParkingById(id);
        if (parking.isPresent()) {
            List<ParkingSpot> parkingSpots = parkingSpotService.getActiveParkingSpots(parking.get());
            return ResponseEntity.ok(parkingSpots.size());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/free-spots-number")
    public ResponseEntity<Integer> getFreeParkingSpotsNumber(@PathVariable Long id,
                                                             @RequestParam OffsetDateTime startDate) {
        Optional<Parking> parking = parkingService.getParkingById(id);
        if (parking.isPresent()) {
            List<ParkingSpot> freeParkingSpots
                    = parkingSpotService.getFreeSpots(parking.get(),
                    startDate,
                    startDate.plusMinutes(15)
            );
            return ResponseEntity.ok(freeParkingSpots.size());
        }
        return ResponseEntity.notFound().build();

    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping
    public ResponseEntity<ParkingDTO> createParking(@RequestBody ParkingDTO parkingDTO) {
        try {
            User user = RequestContext.getUserFromRequest();
            if (user == null) {
                return ResponseEntity.status(401).build();
            }
            User parkingManager = userService.getUserById(user.getId());
            if (!parkingManager.getRole().equals(Role.PARKING_MANAGER)) {
                return ResponseEntity.status(401).build();
            }

            ParkingManager realParkingManager = (ParkingManager) parkingManager;
            if (realParkingManager.getParking() != null) {
                return ResponseEntity.status(405).build();
            }

            Parking createdParking = parkingService.createParking(convertToEntity(parkingDTO));
            realParkingManager.setParking(createdParking);
            userService.updateUser(realParkingManager);

            return ResponseEntity.ok(convertToDTO(createdParking));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PutMapping
    public ResponseEntity<ParkingDTO> updateParking(@RequestBody ParkingDTO parkingDTO) {
        User user = RequestContext.getUserFromRequest();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        ParkingManager parkingManager = (ParkingManager) user;
        if (!parkingManager.getParking().getId().equals(parkingDTO.getId())) {
            return ResponseEntity.status(401).build();
        }

        try {
            Parking updatedParking = parkingService.updateParking(convertToEntity(parkingDTO));
            return ResponseEntity.ok(convertToDTO(updatedParking));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParking(@PathVariable Long id) {
        parkingService.deleteParking(id);
        return ResponseEntity.noContent().build();
    }
}
