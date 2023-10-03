package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.PointDTO;
import net.parkvision.parkvisionbackend.model.Point;
import net.parkvision.parkvisionbackend.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/points")
public class PointController {
    private final PointService _pointService;

    @Autowired
    public PointController(PointService pointService) {
        _pointService = pointService;
    }

    @GetMapping
    public ResponseEntity<List<Point>> getAllPoints() {
        List<Point> points = _pointService.getAllPoints();
        return ResponseEntity.ok(points);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Point> getPointById(@PathVariable Long id) {
        Optional<Point> point = _pointService.getPointById(id);
        return point.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Point> createPoint(@RequestBody PointDTO pointDto) {
        Point createdPoint = _pointService.createPoint(pointDto);
        return ResponseEntity.ok(createdPoint);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Point> updatePoint(@PathVariable Long id, @RequestBody PointDTO pointDto) {
        try {
            Point updatedPoint = _pointService.updatePoint(id, pointDto);
            return ResponseEntity.ok(updatedPoint);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        _pointService.deletePoint(id);
        return ResponseEntity.noContent().build();
    }
}
