package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.config.MessageEncryptor;
import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.repository.DroneRepository;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DroneService {

    private final DroneRepository droneRepository;
    private final ParkingRepository parkingRepository;

    @Autowired
    public DroneService(DroneRepository droneRepository, ParkingRepository parkingRepository) {
        this.droneRepository = droneRepository;
        this.parkingRepository = parkingRepository;
    }

    public List<Drone> getAllDrones() {
        return droneRepository.findAll();
    }

    public List<Drone> getAllDronesByParkingId(Long parkingId) {
        return droneRepository.findAllByParkingId(parkingId);
    }

    public Optional<Drone> getDroneById(Long id) {
        return droneRepository.findById(id);
    }

    public Drone createDrone(Drone drone) {
        if (!parkingRepository.existsById(drone.getParking().getId())) {
            throw new IllegalArgumentException("Parking with ID " + drone.getParking().getId() + " does not exist.");
        }

        drone.setDroneKey(MessageEncryptor.generateKey());
        return droneRepository.save(drone);
    }

    public Drone updateDrone(Drone drone) {
        if (!droneRepository.existsById(drone.getId())) {
            throw new IllegalArgumentException("Drone with ID " + drone.getId() + " does not exist.");
        }

        if (!parkingRepository.existsById(drone.getParking().getId())) {
            throw new IllegalArgumentException("Parking with ID " + drone.getParking().getId() + " does not exist.");
        }

        drone.setName(drone.getName());
        drone.setModel(drone.getModel());
        drone.setSerialNumber(drone.getSerialNumber());
//        drone.setParking(drone.getParking());

        return droneRepository.save(drone);
    }

    public void deleteDrone(Long id) {
        droneRepository.deleteById(id);
    }
}
