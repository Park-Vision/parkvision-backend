package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.ParkingSpotDTO;
import net.parkvision.parkvisionbackend.dto.PointDTO;
import net.parkvision.parkvisionbackend.model.Point;
import net.parkvision.parkvisionbackend.service.PointService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/points")
public class PointController {
    private final PointService pointService;

    private final ModelMapper modelMapper;

    @Autowired
    public PointController(PointService pointService, ModelMapper modelMapper) {
        this.pointService = pointService;
        this.modelMapper = modelMapper;
    }

    public PointDTO convertToDto(Point point) {
        PointDTO pointDTO = modelMapper.map(point, PointDTO.class);
        pointDTO.setParkingSpotDTO(modelMapper.map(point.getParkingSpot(), ParkingSpotDTO.class));
        return pointDTO;
    }

    private Point convertToEntity(PointDTO pointDTO) {
        return modelMapper.map(pointDTO, Point.class);
    }

    @GetMapping
    public ResponseEntity<List<PointDTO>> getAllPoints() {
        List<PointDTO> points = pointService.getAllPoints().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
        return ResponseEntity.ok(points);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointDTO> getPointById(@PathVariable Long id) {
        Optional<Point> point = pointService.getPointById(id);
        return point.map(value -> ResponseEntity.ok(convertToDto(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PointDTO> createPoint(@RequestBody PointDTO pointDto) {
        Point createdPoint = pointService.createPoint(convertToEntity(pointDto));
        return ResponseEntity.ok(convertToDto(createdPoint));
    }

    @PutMapping
    public ResponseEntity<PointDTO> updatePoint(@RequestBody PointDTO pointDto) {
        try {
            Point updatedPoint = pointService.updatePoint(convertToEntity(pointDto));
            return ResponseEntity.ok(convertToDto(updatedPoint));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        pointService.deletePoint(id);
        return ResponseEntity.noContent().build();
    }
}
