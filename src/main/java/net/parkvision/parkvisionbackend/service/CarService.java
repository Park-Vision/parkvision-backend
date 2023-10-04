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

    public Car createCar(Car car) {
        User user = _userRepository.findById(car.getUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("User with ID " + car.getUser().getId() + " does not exist.")
        );
        return _carRepository.save(car);
    }

    //update car
    public Car updateCar(Car car) {
        // find car by id
        if(_carRepository.existsById(car.getId()) && _userRepository.existsById(car.getUser().getId())){
            car.setBrand(car.getBrand());
            car.setColor(car.getColor());
            car.setRegistrationNumber(car.getRegistrationNumber());
            car.setUser(car.getUser());
        }
        return _carRepository.save(car);
    }

    public void deleteCar(Long id) {
        _carRepository.deleteById(id);
    }
}
