package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.dto.ParkingSpotCoordinatesDTO;
import net.parkvision.parkvisionbackend.dto.ParkingSpotDTO;
import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.model.Point;
import net.parkvision.parkvisionbackend.service.DroneService;
import net.parkvision.parkvisionbackend.service.ParkingService;
import net.parkvision.parkvisionbackend.service.ParkingSpotService;
import net.parkvision.parkvisionbackend.service.PointService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parkingspots")
public class ParkingSpotController {

    private final ParkingSpotService _parkingSpotService;

    private final ModelMapper modelMapper;
    private final ParkingService _parkingService;
    private final PointController _pointController;
    private final PointService _pointService;
    private final DroneService _droneService;

    @Autowired
    public ParkingSpotController(ParkingSpotService parkingSpotService, ModelMapper modelMapper,
                                 ParkingService parkingService, PointController pointController,
                                 PointService pointService, DroneService droneService) {
        _parkingSpotService = parkingSpotService;
        this.modelMapper = modelMapper;
        _parkingService = parkingService;
        _pointController = pointController;
        _pointService = pointService;
        _droneService = droneService;
    }

    public ParkingSpotDTO convertToDto(ParkingSpot parkingSpot) {
        ParkingSpotDTO parkingSpotDTO = modelMapper.map(parkingSpot, ParkingSpotDTO.class);
        parkingSpotDTO.setParkingDTO(modelMapper.map(parkingSpot.getParking(), ParkingDTO.class));
        if (!parkingSpot.getPoints().isEmpty()) {
            parkingSpotDTO.setPointsDTO(parkingSpot.getPoints().stream().map(
                    _pointController::convertToDto
            ).collect(Collectors.toList()));
        }
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

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping
    public ResponseEntity<ParkingSpotDTO> createParkingSpot(@RequestBody ParkingSpotDTO parkingSpotDto) {
        ParkingSpot createdParkingSpot = _parkingSpotService.createParkingSpot(convertToEntity(parkingSpotDto));
        return ResponseEntity.ok(convertToDto(createdParkingSpot));
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PutMapping
    public ResponseEntity<ParkingSpotDTO> updateParkingSpot(@RequestBody ParkingSpotDTO parkingSpotDto) {
        try {
            ParkingSpot updatedParkingSpot = _parkingSpotService.updateParkingSpot(convertToEntity(parkingSpotDto));
            return ResponseEntity.ok(convertToDto(updatedParkingSpot));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @DeleteMapping("/soft/{id}")
    public ResponseEntity<Void> softDeleteParkingSpot(@PathVariable Long id) {
        _parkingSpotService.softDeleteParkingSpot(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @DeleteMapping("/hard/{id}")
    public ResponseEntity<Void> hardDeleteParkingSpot(@PathVariable Long id) {
        _parkingSpotService.hardDeleteParkingSpot(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/parking/{id}/free")
    public ResponseEntity<List<ParkingSpotDTO>> getFreeSpotsByParkingId(@PathVariable Long id,
                                                                        @RequestParam String startDate,
                                                                        @RequestParam String endDate) {
        Optional<Parking> parking = _parkingService.getParkingById(id);
        if (parking.isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            List<ParkingSpotDTO> freeParkingSpots
                    = _parkingSpotService.getFreeSpots(parking.get(),
                    ZonedDateTime.parse(startDate, formatter),
                    ZonedDateTime.parse(endDate, formatter)
            ).stream().map(parkingSpot -> {
                        parkingSpot.setPoints(_pointService.getPointsByParkingSpotId(parkingSpot.getId()));
                        return this.convertToDto(parkingSpot);
                    }
            ).collect(Collectors.toList());
            return ResponseEntity.ok(freeParkingSpots);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/parking/{id}")
    public ResponseEntity<List<ParkingSpotDTO>> getSpotsByParkingId(@PathVariable Long id) {
        Optional<Parking> parking = _parkingService.getParkingById(id);
        if (parking.isPresent()) {
            List<ParkingSpotDTO> parkingSpots
                    = _parkingSpotService.getParkingSpots(parking.get()).stream().map(parkingSpot -> {
                        parkingSpot.setPoints(_pointService.getPointsByParkingSpotId(parkingSpot.getId()));
                        return this.convertToDto(parkingSpot);
                    }
            ).collect(Collectors.toList());
            return ResponseEntity.ok(parkingSpots);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping("/parking/{id}/model/create")
    public ResponseEntity<List<ParkingSpotDTO>> createParkingModel(@PathVariable Long id,
                                                                   @RequestBody List<ParkingSpotDTO> parkingSpotDTOList) {
        Optional<Parking> parking = _parkingService.getParkingById(id);
        if (parking.isPresent()) {
            List<ParkingSpotDTO> parkingSpots = _parkingSpotService.createParkingSpots(
                    parkingSpotDTOList.stream().map(this::convertToEntity).collect(Collectors.toList())
            ).stream().map(this::convertToDto).collect(Collectors.toList());
            return ResponseEntity.ok(parkingSpots);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/parking/{id}/free-time")
    public ResponseEntity<Map<Long, ZonedDateTime>> getSpotsFreeTimeByParkingId(@PathVariable Long id,
                                                                                @RequestParam String startDate) {
        Optional<Parking> parking = _parkingService.getParkingById(id);
        if (parking.isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            Map<Long, ZonedDateTime> parkingSpotsWithFreeTime
                    = _parkingSpotService.getSpotsFreeTime(parking.get(), ZonedDateTime.parse(startDate, formatter)
            );
            return ResponseEntity.ok(parkingSpotsWithFreeTime);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/drone/{id}")
    public ResponseEntity<List<ParkingSpotCoordinatesDTO>> getSpotCoordinatesByDroneId(@PathVariable Long id) {
        Optional<Drone> drone = _droneService.getDroneById(id);
        if (drone.isPresent()) {
            List<ParkingSpotCoordinatesDTO> parkingSpotCoordinatesList = new ArrayList<>();

            List<ParkingSpot> parkingSpots = _parkingSpotService.getParkingSpots(drone.get());
            for (ParkingSpot parkingSpot : parkingSpots) {
                List<Point> points = _pointService.getPointsByParkingSpotId(parkingSpot.getId());
                if (!points.isEmpty()) {
                    ParkingSpotCoordinatesDTO parkingSpotCoordinatesDTO = getParkingSpotCoordinatesDTO(parkingSpot, points);

                    parkingSpotCoordinatesList.add(parkingSpotCoordinatesDTO);
                }
            }
            return ResponseEntity.ok(parkingSpotCoordinatesList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private static ParkingSpotCoordinatesDTO getParkingSpotCoordinatesDTO(ParkingSpot parkingSpot, List<Point> points) {
        double centerLongitude = 0.0;
        double centerLatitude = 0.0;

        for (Point point : points) {
            centerLongitude += point.getLongitude();
            centerLatitude += point.getLatitude();
        }

        int numPoints = points.size();
        centerLongitude /= numPoints;
        centerLatitude /= numPoints;

        ParkingSpotCoordinatesDTO parkingSpotCoordinatesDTO = new ParkingSpotCoordinatesDTO();
        parkingSpotCoordinatesDTO.setParkingSpotId(parkingSpot.getId());
        parkingSpotCoordinatesDTO.setCenterLongitude(centerLongitude);
        parkingSpotCoordinatesDTO.setCenterLatitude(centerLatitude);
        return parkingSpotCoordinatesDTO;
    }
}
