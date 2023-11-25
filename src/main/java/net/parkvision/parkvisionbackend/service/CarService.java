package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Car;
import net.parkvision.parkvisionbackend.repository.CarRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Autowired
    public CarService(UserRepository userRepository, CarRepository carRepository) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public Car createCar(Car car) {
        if (!userRepository.existsById(car.getClient().getId())) {
            throw new IllegalArgumentException("User with ID " + car.getClient().getId() + " does not exist.");
        }

        return carRepository.save(car);
    }

    public Car updateCar(Car car) {
        if (carRepository.existsById(car.getId()) && userRepository.existsById(car.getClient().getId())) {
            car.setBrand(car.getBrand());
            car.setColor(car.getColor());
            car.setRegistrationNumber(car.getRegistrationNumber());
            car.setClient(car.getClient());
        }
        return carRepository.save(car);
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    public List<Car> getAllCarsByClientId(Long id) {
        return carRepository.findAllByClientId(id);
    }
}
