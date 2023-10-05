package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.service.ParkingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parkings")
public class ParkingController {

    private final ParkingService _parkingService;

    private final ModelMapper modelMapper;

    @Autowired
    public ParkingController(ParkingService parkingService, ModelMapper modelMapper) {
        _parkingService = parkingService;
        this.modelMapper = modelMapper;
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
        return ResponseEntity.ok(parkings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingDTO> getParkingById(@PathVariable Long id) {
        Optional<Parking> parking = _parkingService.getParkingById(id);
        if (parking.isPresent()) {
            return ResponseEntity.ok(convertToDTO(parking.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ParkingDTO> createParking(@RequestBody ParkingDTO parkingDTO) {
        Parking createdParking = _parkingService.createParking(convertToEntity(parkingDTO));
        return ResponseEntity.ok(convertToDTO(createdParking));
    }

    @PutMapping
    public ResponseEntity<ParkingDTO> updateParking(@RequestBody ParkingDTO parkingDTO) {
        try {
            Parking updatedParking = _parkingService.updateParking(convertToEntity(parkingDTO));
            return ResponseEntity.ok(convertToDTO(updatedParking));
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
