package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.CarDTO;
import net.parkvision.parkvisionbackend.model.Car;
import net.parkvision.parkvisionbackend.service.CarService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    private final CarService _carService;
    private final ModelMapper modelMapper;
    @Autowired
    public CarController(CarService carService, ModelMapper modelMapper) {
        _carService = carService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<CarDTO> cars = _carService.getAllCars().stream().map(car -> {
            CarDTO carDTO = modelMapper.map(car, CarDTO.class);
            carDTO.setClientId(car.getUser().getId());
            return carDTO;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
        Optional<Car> car = _carService.getCarById(id);
        if (car.isPresent()){
            CarDTO carDTO = modelMapper.map(car.get(), CarDTO.class);
            carDTO.setClientId(car.get().getUser().getId());
            return ResponseEntity.ok(carDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody CarDTO carDTO) {
        Car createdCar = _carService.createCar(carDTO);
        return ResponseEntity.ok(createdCar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody CarDTO carDto) {
        try {
            Car updatedCar = _carService.updateCar(id, carDto);
            return ResponseEntity.ok(updatedCar);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        _carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
