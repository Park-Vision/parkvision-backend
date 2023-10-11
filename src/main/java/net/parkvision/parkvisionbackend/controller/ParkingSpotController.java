package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.dto.ParkingSpotDTO;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.service.ParkingService;
import net.parkvision.parkvisionbackend.service.ParkingSpotService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parkingspots")
public class ParkingSpotController {

    private final ParkingSpotService _parkingSpotService;

    private final ModelMapper modelMapper;
    private final ParkingService _parkingService;

    @Autowired
    public ParkingSpotController(ParkingSpotService parkingSpotService, ModelMapper modelMapper,
                                 ParkingService parkingService) {
        _parkingSpotService = parkingSpotService;
        this.modelMapper = modelMapper;
        _parkingService = parkingService;
    }

    public ParkingSpotDTO convertToDto(ParkingSpot parkingSpot) {
        ParkingSpotDTO parkingSpotDTO = modelMapper.map(parkingSpot, ParkingSpotDTO.class);
        parkingSpotDTO.setParkingDTO(modelMapper.map(parkingSpot.getParking(), ParkingDTO.class));
        return parkingSpotDTO;
    }

    private ParkingSpot convertToEntity(ParkingSpotDTO parkingSpotDTO) {
        return modelMapper.map(parkingSpotDTO, ParkingSpot.class);
    }

    @GetMapping
    public ResponseEntity<List<ParkingSpotDTO>> getAllParkingSpots() {
        List<ParkingSpotDTO> parkingSpots
                = _parkingSpotService.getAllParkingSpots().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
        return ResponseEntity.ok(parkingSpots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpotDTO> getParkingSpotById(@PathVariable Long id) {
        Optional<ParkingSpot> parkingSpot = _parkingSpotService.getParkingSpotById(id);
        if (parkingSpot.isPresent()) {
            return ResponseEntity.ok(convertToDto(parkingSpot.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ParkingSpotDTO> createParkingSpot(@RequestBody ParkingSpotDTO parkingSpotDto) {
        ParkingSpot createdParkingSpot = _parkingSpotService.createParkingSpot(convertToEntity(parkingSpotDto));
        return ResponseEntity.ok(convertToDto(createdParkingSpot));
    }

    @PutMapping
    public ResponseEntity<ParkingSpotDTO> updateParkingSpot(@RequestBody ParkingSpotDTO parkingSpotDto) {
        try {
            ParkingSpot updatedParkingSpot = _parkingSpotService.updateParkingSpot(convertToEntity(parkingSpotDto));
            return ResponseEntity.ok(convertToDto(updatedParkingSpot));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/soft/{id}")
    public ResponseEntity<Void> softDeleteParkingSpot(@PathVariable Long id) {
        _parkingSpotService.softDeleteParkingSpot(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/hard/{id}")
    public ResponseEntity<Void> hardDeleteParkingSpot(@PathVariable Long id) {
        _parkingSpotService.hardDeleteParkingSpot(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/parking/{id}/free")
    public ResponseEntity<List<ParkingSpotDTO>> getFreeSpotsByParkingId(@PathVariable Long id,
                                                                        @RequestParam String startDate,
                                                                        @RequestParam String endDate) throws ParseException {
        Optional<Parking> parking = _parkingService.getParkingById(id);
        if (parking.isPresent()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-dd hh:mm:ss", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("Poland/Warsaw"));
            List<ParkingSpotDTO> freeParkingSpots
                    = _parkingSpotService.getFreeSpots(parking.get(), formatter.parse(startDate),
                    formatter.parse(endDate)).stream().map(
                    this::convertToDto
            ).collect(Collectors.toList());
            return ResponseEntity.ok(freeParkingSpots);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/parking/{id}")
    public ResponseEntity<List<ParkingSpotDTO>> getFreeSpotsByParkingId(@PathVariable Long id) {
        Optional<Parking> parking = _parkingService.getParkingById(id);
        if (parking.isPresent()) {
            List<ParkingSpotDTO> parkingSpots
                    = _parkingSpotService.getParkingSpots(parking.get()).stream().map(
                    this::convertToDto
            ).collect(Collectors.toList());
            return ResponseEntity.ok(parkingSpots);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
