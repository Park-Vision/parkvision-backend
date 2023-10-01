package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Car;
import net.parkvision.parkvisionbackend.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private final CarRepository _carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this._carRepository = carRepository;
    }

    public List<Car> getAllCars() {
        return _carRepository.findAll();
    }

    public Optional<Car> getCarById(Long id) {
        return _carRepository.findById(id);
    }

    public Car createCar(Car car) {
        return _carRepository.save(car);
    }

    //update car
    public Car updateCar(Long id, Car car){
        if (_carRepository.existsById(id)) {
            car.setId(id);
            return _carRepository.save(car);
        } else {
            throw new IllegalArgumentException("Car with ID " + id + " does not exist.");
        }
    }

    public void deleteCar(Long id) {
        _carRepository.deleteById(id);
    }
}
