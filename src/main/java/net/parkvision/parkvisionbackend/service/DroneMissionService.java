package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.dto.DroneMissionDTO;
import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.model.DroneMission;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.repository.DroneMissionRepository;
import net.parkvision.parkvisionbackend.repository.DroneRepository;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DroneMissionService {

    private final DroneMissionRepository _droneMissionRepository;
    private final ParkingRepository _parkingRepository;
    private final DroneRepository _droneRepository;
    private final ModelMapper modelMapper;


    @Autowired
    public DroneMissionService(DroneMissionRepository droneMissionRepository, ParkingRepository parkingRepository, DroneRepository droneRepository, ModelMapper modelMapper) {
        this._droneMissionRepository = droneMissionRepository;
        _parkingRepository = parkingRepository;
        _droneRepository = droneRepository;
        this.modelMapper = modelMapper;
    }

    public List<DroneMission> getAllDroneMissions() {
        return _droneMissionRepository.findAll();
    }

    public Optional<DroneMission> getDroneMissionById(Long id) {
        return _droneMissionRepository.findById(id);
    }

    public DroneMission createDroneMission(DroneMissionDTO droneMissionDto) {
        Parking parking = _parkingRepository.findById(droneMissionDto.getParkingId()).orElseThrow(
                () -> new IllegalArgumentException("Parking with ID " + droneMissionDto.getParkingId() + " does not exist.")
        );
        Drone drone = _droneRepository.findById(droneMissionDto.getDroneId()).orElseThrow(
                () -> new IllegalArgumentException("Drone with ID " + droneMissionDto.getDroneId() + " does not exist.")
        );

        DroneMission droneMission = modelMapper.map(droneMissionDto, DroneMission.class);
        droneMission.setParking(parking);
        droneMission.setDrone(drone);
        return _droneMissionRepository.save(droneMission);
    }

    public DroneMission updateDroneMission(Long id, DroneMissionDTO droneMissionDto){
        DroneMission droneMission = _droneMissionRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("DroneMission with ID " + id + " does not exist.")
        );

        Parking parking = _parkingRepository.findById(droneMissionDto.getParkingId()).orElseThrow(
                () -> new IllegalArgumentException("Parking with ID " + droneMissionDto.getParkingId() + " does not exist.")
        );

        Drone drone = _droneRepository.findById(droneMissionDto.getDroneId()).orElseThrow(
                () -> new IllegalArgumentException("Drone with ID " + droneMissionDto.getDroneId() + " does not exist.")
        );

        droneMission.setMissionName(droneMissionDto.getMissionName());
        droneMission.setMissionDescription(droneMissionDto.getMissionDescription());
        droneMission.setMissionStatus(droneMissionDto.getMissionStatus());
        droneMission.setMissionStartDate(droneMissionDto.getMissionStartDate());
        droneMission.setMissionEndDate(droneMissionDto.getMissionEndDate());
        droneMission.setMissionStatus(droneMissionDto.getMissionStatus());

        droneMission.setParking(parking);
        droneMission.setDrone(drone);

        return _droneMissionRepository.save(droneMission);
    }

    public void deleteDroneMission(Long id) {
        _droneMissionRepository.deleteById(id);
    }
}
