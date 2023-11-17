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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parkings")
public class ParkingController {

    private final ParkingService _parkingService;
    private final ParkingSpotService _parkingSpotService;

    private final UserService _userService;
    private final ModelMapper modelMapper;

    @Autowired
    public ParkingController(ParkingService parkingService, ParkingSpotService parkingSpotService,
                             ModelMapper modelMapper, UserService userService) {
        _parkingService = parkingService;
        this._parkingSpotService = parkingSpotService;
        this.modelMapper = modelMapper;
        this._userService = userService;
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
                = _parkingService.getAllParkings().stream().map(
                this::convertToDTO
        ).collect(Collectors.toList());
        User user = RequestContext.getUserFromRequest();
        if( user == null || user.getRole().equals(Role.USER)){
            return ResponseEntity.ok(parkings);
        }
        List<ParkingDTO> filteredParking =
        parkings.stream()
                .filter(parkingDTO ->
                        parkingDTO.getId().equals(((ParkingModerator) user).getParking().getId())).toList();
        return ResponseEntity.ok(filteredParking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingDTO> getParkingById(@PathVariable Long id) {
        User user = RequestContext.getUserFromRequest();
        Optional<Parking> parking = _parkingService.getParkingById(id);
        if( user == null || user.getRole().equals(Role.USER)){
            return parking.map(value -> ResponseEntity.ok(convertToDTO(value))).orElseGet(() -> ResponseEntity.notFound().build());
        }
        else if (user.getRole().equals(Role.PARKING_MANAGER)) {
            if (parking.isPresent()){
                ParkingModerator parkingModerator = (ParkingModerator) user;
                if((parkingModerator.getParking().getId().equals(parking.get().getId()))){
                    return ResponseEntity.ok(convertToDTO(parking.get()));
                }
                else{
                    return ResponseEntity.status(401).build();
                }
            }
            else{
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/spots-number")
    public ResponseEntity<Integer> getParkingSpotsNumber(@PathVariable Long id) {
        Optional<Parking> parking = _parkingService.getParkingById(id);
        if (parking.isPresent()) {
            List<ParkingSpot> parkingSpots = _parkingSpotService.getActiveParkingSpots(parking.get());
            return ResponseEntity.ok(parkingSpots.size());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/free-spots-number")
    public ResponseEntity<Integer> getFreeParkingSpotsNumber(@PathVariable Long id, @RequestParam OffsetDateTime startDate) {
        Optional<Parking> parking = _parkingService.getParkingById(id);
        if (parking.isPresent()) {
            List<ParkingSpot> freeParkingSpots
                    = _parkingSpotService.getFreeSpots(parking.get(),
                    startDate,
                    startDate.plusMinutes(15)
            );
            return ResponseEntity.ok(freeParkingSpots.size());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping
    public ResponseEntity<ParkingDTO> createParking(@RequestBody ParkingDTO parkingDTO) {
        User user = RequestContext.getUserFromRequest();
        if(user == null ){
            return ResponseEntity.status(401).build();
        }
        Parking createdParking = _parkingService.createParking(convertToEntity(parkingDTO));
        User parkingModerator = _userService.getUserById(user.getId());
        if (parkingModerator.getRole().equals(Role.PARKING_MANAGER)) {
            ParkingModerator realParkingModerator = (ParkingModerator) parkingModerator;
            realParkingModerator.setParking(createdParking);
            _userService.updateUser(realParkingModerator);
        }
        else{
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(convertToDTO(createdParking));
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PutMapping
    public ResponseEntity<ParkingDTO> updateParking(@RequestBody ParkingDTO parkingDTO) {
        User user = RequestContext.getUserFromRequest();
        if(user == null ){
            return ResponseEntity.status(401).build();
        }
        ParkingModerator parkingModerator = (ParkingModerator) user;
        if(!parkingModerator.getParking().getId().equals(parkingDTO.getId())){
            return ResponseEntity.status(401).build();
        }

        try {
            Parking updatedParking = _parkingService.updateParking(convertToEntity(parkingDTO));
            return ResponseEntity.ok(convertToDTO(updatedParking));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParking(@PathVariable Long id) {
        _parkingService.deleteParking(id);
        return ResponseEntity.noContent().build();
    }
}
