package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.dto.CarDTO;
import net.parkvision.parkvisionbackend.model.Car;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.CarRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private final UserRepository _userRepository;
    private final CarRepository _carRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CarService(UserRepository userRepository, CarRepository carRepository, ModelMapper modelMapper) {
        _userRepository = userRepository;
        this._carRepository = carRepository;
        this.modelMapper = modelMapper;
    }

    public List<Car> getAllCars() {
        return _carRepository.findAll();
    }

    public Optional<Car> getCarById(Long id) {
        return _carRepository.findById(id);
    }

    public Car createCar(CarDTO car) {
        User user  = _userRepository.findById(car.getClientId()).orElseThrow(
                () -> new IllegalArgumentException("User with ID " + car.getClientId() + " does not exist.")
        );
        Car newCar = modelMapper.map(car, Car.class);
        newCar.setUser(user);
        return _carRepository.save(newCar);
    }

    //update car
    public Car updateCar(Long id, CarDTO carDto){
        // find car by id
        Car car = _carRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Car with ID " + id + " does not exist.")
        );
        // find user by id
        User user = _userRepository.findById(carDto.getClientId()).orElseThrow(
                () -> new IllegalArgumentException("User with ID " + carDto.getClientId() + " does not exist.")
        );
        // update car
        car.setBrand(carDto.getBrand());
        car.setColor(carDto.getColor());
        car.setRegistrationNumber(carDto.getRegistrationNumber());
        car.setUser(user);
        return _carRepository.save(car);
    }

    public void deleteCar(Long id) {
        _carRepository.deleteById(id);
    }
}
