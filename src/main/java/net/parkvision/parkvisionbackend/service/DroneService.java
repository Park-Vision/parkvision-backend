package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.repository.DroneRepository;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DroneService {

    private final DroneRepository _droneRepository;
    private final ParkingRepository _parkingRepository;

    @Autowired
    public DroneService(DroneRepository droneRepository, ParkingRepository parkingRepository) {
        this._droneRepository = droneRepository;
        _parkingRepository = parkingRepository;
    }

    public List<Drone> getAllDrones() {
        return _droneRepository.findAll();
    }

    public Optional<Drone> getDroneById(Long id) {
        return _droneRepository.findById(id);
    }

    public Drone createDrone(Drone drone) {
        if (!_parkingRepository.existsById(drone.getParking().getId())) {
            throw new IllegalArgumentException("Parking with ID " + drone.getParking().getId() + " does not exist.");
        }
        return _droneRepository.save(drone);
    }

    public Drone updateDrone(Drone drone) {
        if (!_droneRepository.existsById(drone.getId())) {
            throw new IllegalArgumentException("Drone with ID " + drone.getId() + " does not exist.");
        }

        if (!_parkingRepository.existsById(drone.getParking().getId())) {
            throw new IllegalArgumentException("Parking with ID " + drone.getParking().getId() + " does not exist.");
        }

        drone.setName(drone.getName());
        drone.setModel(drone.getModel());
        drone.setSerialNumber(drone.getSerialNumber());
        drone.setParking(drone.getParking());

        return _droneRepository.save(drone);
    }

    public void deleteDrone(Long id) {
        _droneRepository.deleteById(id);
    }

}
