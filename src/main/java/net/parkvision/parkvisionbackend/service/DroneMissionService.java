package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.DroneMission;
import net.parkvision.parkvisionbackend.repository.DroneMissionRepository;
import net.parkvision.parkvisionbackend.repository.DroneRepository;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DroneMissionService {

    private final DroneMissionRepository _droneMissionRepository;
    private final ParkingRepository _parkingRepository;
    private final DroneRepository _droneRepository;


    @Autowired
    public DroneMissionService(DroneMissionRepository droneMissionRepository, ParkingRepository parkingRepository,
                               DroneRepository droneRepository) {
        this._droneMissionRepository = droneMissionRepository;
        _parkingRepository = parkingRepository;
        _droneRepository = droneRepository;
    }

    public List<DroneMission> getAllDroneMissions() {
        return _droneMissionRepository.findAll();
    }

    public Optional<DroneMission> getDroneMissionById(Long id) {
        return _droneMissionRepository.findById(id);
    }

    public DroneMission createDroneMission(DroneMission droneMission) {
        if (!_parkingRepository.existsById(droneMission.getParking().getId())) {
            throw new IllegalArgumentException("Parking with ID " + droneMission.getParking().getId() + " does not " +
                    "exist.");
        }

        if (!_droneRepository.existsById(droneMission.getDrone().getId())) {
            throw new IllegalArgumentException("Drone with ID " + droneMission.getDrone().getId() + " does not exist.");
        }

        return _droneMissionRepository.save(droneMission);
    }

    public DroneMission updateDroneMission(DroneMission droneMission) {

        if (!_droneMissionRepository.existsById(droneMission.getId())) {
            throw new IllegalArgumentException("DroneMission with ID " + droneMission.getId() + " does not exist.");
        }

        if (!_parkingRepository.existsById(droneMission.getParking().getId())) {
            throw new IllegalArgumentException("Parking with ID " + droneMission.getParking().getId() + " does not " +
                    "exist.");
        }

        if (!_droneRepository.existsById(droneMission.getDrone().getId())) {
            throw new IllegalArgumentException("Drone with ID " + droneMission.getDrone().getId() + " does not exist.");
        }

        droneMission.setMissionName(droneMission.getMissionName());
        droneMission.setMissionDescription(droneMission.getMissionDescription());
        droneMission.setMissionStatus(droneMission.getMissionStatus());
        droneMission.setMissionStartDate(droneMission.getMissionStartDate());
        droneMission.setMissionEndDate(droneMission.getMissionEndDate());
        droneMission.setMissionStatus(droneMission.getMissionStatus());

        return _droneMissionRepository.save(droneMission);
    }

    public void deleteDroneMission(Long id) {
        _droneMissionRepository.deleteById(id);
    }
}
