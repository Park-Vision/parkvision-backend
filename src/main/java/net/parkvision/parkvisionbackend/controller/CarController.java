package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.CarDTO;
import net.parkvision.parkvisionbackend.dto.ClientDTO;
import net.parkvision.parkvisionbackend.model.Car;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.service.CarService;
import net.parkvision.parkvisionbackend.service.RequestContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    private final CarService carService;
    private final ModelMapper modelMapper;

    @Autowired
    public CarController(CarService carService, ModelMapper modelMapper) {
        this.carService = carService;
        this.modelMapper = modelMapper;
    }

    private CarDTO convertToDTO(Car car) {
        CarDTO carDTO = modelMapper.map(car, CarDTO.class);
        carDTO.setClientDTO(modelMapper.map(car.getClient(), ClientDTO.class));
        return carDTO;
    }

    private Car convertToEntity(CarDTO carDTO) {
        return modelMapper.map(carDTO, Car.class);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<CarDTO> cars =
                carService.getAllCars().stream().map(
                        this::convertToDTO
                ).collect(Collectors.toList());
        return ResponseEntity.ok(cars);
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/client")
    public ResponseEntity<List<CarDTO>> getAllCarsByClientId() {
        User client = RequestContext.getUserFromRequest();
        if(client == null){
            return ResponseEntity.badRequest().build();
        }
        List<CarDTO> cars =
                carService.getAllCarsByClientId(client.getId()).stream().map(
                        this::convertToDTO
                ).collect(Collectors.toList());
        return ResponseEntity.ok(cars);
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
        User client = RequestContext.getUserFromRequest();
        Optional<Car> car = carService.getCarById(id);
        if (car.isPresent() && client.getId().equals(car.get().getClient().getId())) {
            return ResponseEntity.ok(convertToDTO(car.get()));
        }
        return ResponseEntity.notFound().build();
    }


    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<CarDTO> createCar(@RequestBody CarDTO carDTO) {
        User client = RequestContext.getUserFromRequest();
        if(!client.getId().equals(carDTO.getClientDTO().getId())){
            return ResponseEntity.badRequest().build();
        }

        Car carCreated = carService.createCar(convertToEntity(carDTO));
        return ResponseEntity.ok(convertToDTO(carCreated));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping
    public ResponseEntity<CarDTO> updateCar(@RequestBody CarDTO carDTO) {

        User client = RequestContext.getUserFromRequest();
        if(client == null){
            return ResponseEntity.badRequest().build();
        }

        Optional<Car> car = carService.getCarById(carDTO.getId());
        if(!car.isPresent()){
            return ResponseEntity.notFound().build();
        } else {
            if(!car.get().getClient().getId().equals(client.getId())){
                return ResponseEntity.badRequest().build();
            }
        }

        try {
            Car carUpdated = carService.updateCar(convertToEntity(carDTO));
            return ResponseEntity.ok(convertToDTO(carUpdated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {

        User client = RequestContext.getUserFromRequest();
        if(client == null){
            return ResponseEntity.badRequest().build();
        }

        Optional<Car> car = carService.getCarById(id);
        if(!car.isPresent()){
            return ResponseEntity.notFound().build();
        } else {
            if(!car.get().getClient().getId().equals(client.getId())){
                return ResponseEntity.badRequest().build();
            }
        }

        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
