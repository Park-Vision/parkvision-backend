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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parkingspots")
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;
    private final ModelMapper modelMapper;
    private final ParkingService parkingService;
    private final PointController pointController;
    private final PointService pointService;
    private final DroneService droneService;

    @Autowired
    public ParkingSpotController(ParkingSpotService parkingSpotService, ModelMapper modelMapper,
                                 ParkingService parkingService, PointController pointController,
                                 PointService pointService, DroneService droneService) {
        this.parkingSpotService = parkingSpotService;
        this.modelMapper = modelMapper;
        this.parkingService = parkingService;
        this.pointController = pointController;
        this.pointService = pointService;
        this.droneService = droneService;
    }

    public ParkingSpotDTO convertToDto(ParkingSpot parkingSpot) {
        ParkingSpotDTO parkingSpotDTO = modelMapper.map(parkingSpot, ParkingSpotDTO.class);
        parkingSpotDTO.setParkingDTO(modelMapper.map(parkingSpot.getParking(), ParkingDTO.class));
        if (!parkingSpot.getPoints().isEmpty()) {
            parkingSpotDTO.setPointsDTO(parkingSpot.getPoints().stream().map(
                    pointController::convertToDto
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
                = parkingSpotService.getAllParkingSpotsWithPoints().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
        return ResponseEntity.ok(parkingSpots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpotDTO> getParkingSpotById(@PathVariable Long id) {
        Optional<ParkingSpot> parkingSpot = parkingSpotService.getParkingSpotById(id);
        if (parkingSpot.isPresent()) {
            parkingSpot.get().setPoints(pointService.getPointsByParkingSpotId(parkingSpot.get().getId()));
            return ResponseEntity.ok(convertToDto(parkingSpot.get()));
        }
        return ResponseEntity.notFound().build();

    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping
    public ResponseEntity<ParkingSpotDTO> createParkingSpot(@RequestBody ParkingSpotDTO parkingSpotDto) {
        ParkingSpot createdParkingSpot = parkingSpotService.createParkingSpot(convertToEntity(parkingSpotDto));
        return ResponseEntity.ok(convertToDto(createdParkingSpot));
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PutMapping
    public ResponseEntity<ParkingSpotDTO> updateParkingSpot(@RequestBody ParkingSpotDTO parkingSpotDto) {
        try {
            ParkingSpot updatedParkingSpot = parkingSpotService.updateParkingSpot(convertToEntity(parkingSpotDto));
            return ResponseEntity.ok(convertToDto(updatedParkingSpot));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @DeleteMapping("/soft/{id}")
    public ResponseEntity<Void> softDeleteParkingSpot(@PathVariable Long id) {
        try {
            parkingSpotService.softDeleteParkingSpot(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @DeleteMapping("/hard/{id}")
    public ResponseEntity<Void> hardDeleteParkingSpot(@PathVariable Long id) {
        parkingSpotService.hardDeleteParkingSpot(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/parking/{id}/free")
    public ResponseEntity<List<ParkingSpotDTO>> getFreeSpotsByParkingId(@PathVariable Long id,
                                                                        @RequestParam OffsetDateTime startDate,
                                                                        @RequestParam OffsetDateTime endDate) {
        Optional<Parking> parking = parkingService.getParkingById(id);
        if (parking.isPresent()) {
            startDate = startDate.withOffsetSameInstant(parking.get().getTimeZone());
            endDate = endDate.withOffsetSameInstant(parking.get().getTimeZone());
            List<ParkingSpotDTO> freeParkingSpots
                    = parkingSpotService.getFreeSpots(parking.get(),
                    startDate,
                    endDate
            ).stream().map(parkingSpot -> {
                        parkingSpot.setPoints(pointService.getPointsByParkingSpotId(parkingSpot.getId()));
                        return this.convertToDto(parkingSpot);
                    }
            ).collect(Collectors.toList());
            return ResponseEntity.ok(freeParkingSpots);
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/parking/{id}")
    public ResponseEntity<List<ParkingSpotDTO>> getSpotsByParkingId(@PathVariable Long id) {
        Optional<Parking> parking = parkingService.getParkingById(id);
        if (parking.isPresent()) {
            List<ParkingSpotDTO> parkingSpots
                    = parkingSpotService.getParkingSpotsWithPoints(parking.get()).stream().map(parkingSpot -> {
                        parkingSpot.setPoints(pointService.getPointsByParkingSpotId(parkingSpot.getId()));
                        return this.convertToDto(parkingSpot);
                    }
            ).collect(Collectors.toList());
            return ResponseEntity.ok(parkingSpots);
        }
        return ResponseEntity.notFound().build();

    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER')")
    @PostMapping("/parking/{id}/model/create")
    public ResponseEntity<List<ParkingSpotDTO>> createParkingModel(@PathVariable Long id,
                                                                   @RequestBody List<ParkingSpotDTO> parkingSpotDTOList) {
        Optional<Parking> parking = parkingService.getParkingById(id);
        if (parking.isPresent()) {
            List<ParkingSpotDTO> parkingSpots = parkingSpotService.createParkingSpots(
                    parkingSpotDTOList.stream().map(this::convertToEntity).collect(Collectors.toList())
            ).stream().map(this::convertToDto).collect(Collectors.toList());
            return ResponseEntity.ok(parkingSpots);
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/parking/{id}/free-time")
    public ResponseEntity<Map<Long, Map<String, OffsetDateTime>>> getSpotsFreeTimeByParkingId(@PathVariable Long id,
                                                                                              @RequestParam OffsetDateTime startDate) {
        Optional<Parking> parking = parkingService.getParkingById(id);
        if (parking.isPresent()) {
            startDate = startDate.withOffsetSameInstant(parking.get().getTimeZone());
            Map<Long, Map<String, OffsetDateTime>> parkingSpotsWithFreeTime
                    = parkingSpotService.getSpotsFreeTime(parking.get(), startDate);
            return ResponseEntity.ok(parkingSpotsWithFreeTime);
        }
        return ResponseEntity.notFound().build();

    }

    public List<ParkingSpotCoordinatesDTO> getSpotCoordinatesByDroneId(Long id) {
        Optional<Drone> drone = droneService.getDroneById(id);
        if (drone.isPresent()) {
            List<ParkingSpotCoordinatesDTO> parkingSpotCoordinatesList = new ArrayList<>();

            List<ParkingSpot> parkingSpots = parkingSpotService.getParkingSpots(drone.get());
            for (ParkingSpot parkingSpot : parkingSpots) {
                List<Point> points = pointService.getPointsByParkingSpotId(parkingSpot.getId());
                if (!points.isEmpty()) {
                    ParkingSpotCoordinatesDTO parkingSpotCoordinatesDTO = getParkingSpotCoordinatesDTO(parkingSpot,
                            points);

                    parkingSpotCoordinatesList.add(parkingSpotCoordinatesDTO);
                }
            }
            return parkingSpotCoordinatesList;
        }
        return null;
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
