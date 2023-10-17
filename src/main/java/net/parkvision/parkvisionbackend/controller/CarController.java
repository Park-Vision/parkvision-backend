package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.CarDTO;
import net.parkvision.parkvisionbackend.dto.ClientDTO;
import net.parkvision.parkvisionbackend.model.Car;
import net.parkvision.parkvisionbackend.model.Client;
import net.parkvision.parkvisionbackend.service.CarService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private CarDTO convertToDTO(Car car) {
        CarDTO carDTO = modelMapper.map(car, CarDTO.class);
        carDTO.setClientDTO(modelMapper.map(car.getClient(), ClientDTO.class));
        return carDTO;
    }

    private Car convertToEntity(CarDTO carDTO) {
        return modelMapper.map(carDTO, Car.class);
    }

    //api/cars
    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<CarDTO> cars =
                _carService.getAllCars().stream().map(
                        this::convertToDTO
                ).collect(Collectors.toList());
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/client")
    public ResponseEntity<List<CarDTO>> getAllCarsByClientId() {
        Object user = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(user instanceof Client) {
            Client client = (Client) user;
            List<CarDTO> cars =
                    _carService.getAllCarsByClientId(client.getId()).stream().map(
                            this::convertToDTO
                    ).collect(Collectors.toList());
            return ResponseEntity.ok(cars);
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('PARKING_MANAGER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
        Client client = getClientFromRequest();
        Optional<Car> car = _carService.getCarById(id);
        if (car.isPresent() && client.getId().equals(car.get().getClient().getId())) {
            return ResponseEntity.ok(convertToDTO(car.get()));
        }
        return ResponseEntity.notFound().build();
    }


    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CarDTO> createCar(@RequestBody CarDTO carDTO) {
        Client client = getClientFromRequest();
        if(!client.getId().equals(carDTO.getClientDTO().getId())){
            return ResponseEntity.badRequest().build();
        }

        Car carCreated = _carService.createCar(convertToEntity(carDTO));
        return ResponseEntity.ok(convertToDTO(carCreated));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping
    public ResponseEntity<CarDTO> updateCar(@RequestBody CarDTO carDTO) {

        Client client = getClientFromRequest();
        if(client == null){
            return ResponseEntity.badRequest().build();
        }

        Optional<Car> car = _carService.getCarById(carDTO.getId());
        if(!car.isPresent()){
            return ResponseEntity.notFound().build();
        } else {
            if(!car.get().getClient().getId().equals(client.getId())){
                return ResponseEntity.badRequest().build();
            }
        }

        try {
            Car carUpdated = _carService.updateCar(convertToEntity(carDTO));
            return ResponseEntity.ok(convertToDTO(carUpdated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {

        Client client = getClientFromRequest();
        if(client == null){
            return ResponseEntity.badRequest().build();
        }

        Optional<Car> car = _carService.getCarById(id);
        if(!car.isPresent()){
            return ResponseEntity.notFound().build();
        } else {
            if(!car.get().getClient().getId().equals(client.getId())){
                return ResponseEntity.badRequest().build();
            }
        }

        _carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    private Client getClientFromRequest(){
        Object user = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(user instanceof Client) {
            return (Client) user;
        }
        return null;
    }
}
