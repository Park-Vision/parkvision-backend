package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.repository.DroneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DroneService {

    private final DroneRepository _droneRepository;

    @Autowired
    public DroneService(DroneRepository droneRepository) {
        this._droneRepository = droneRepository;
    }

    public List<Drone> getAllDrones() {
        return _droneRepository.findAll();
    }

    public Optional<Drone> getDroneById(Long id) {
        return _droneRepository.findById(id);
    }

    public Drone createDrone(Drone drone) {
        return _droneRepository.save(drone);
    }

    //update drone
    public Drone updateDrone(Long id, Drone drone){
        if (_droneRepository.existsById(id)) {
            drone.setId(id);
            return _droneRepository.save(drone);
        } else {
            throw new IllegalArgumentException("Drone with ID " + id + " does not exist.");
        }
    }

    public void deleteDrone(Long id) {
        _droneRepository.deleteById(id);
    }

}
