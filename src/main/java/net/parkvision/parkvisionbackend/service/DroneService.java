package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.dto.DroneDTO;
import net.parkvision.parkvisionbackend.model.Drone;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.repository.DroneRepository;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DroneService {

    private final DroneRepository _droneRepository;
    private final ParkingRepository _parkingRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public DroneService(DroneRepository droneRepository, ParkingRepository parkingRepository, ModelMapper modelMapper) {
        this._droneRepository = droneRepository;
        _parkingRepository = parkingRepository;
        this.modelMapper = modelMapper;
    }

    public List<Drone> getAllDrones() {
        return _droneRepository.findAll();
    }

    public Optional<Drone> getDroneById(Long id) {
        return _droneRepository.findById(id);
    }

    public Drone createDrone(DroneDTO droneDto) {
        Parking parking = _parkingRepository.findById(droneDto.getParkingId()).orElseThrow(
                () -> new IllegalArgumentException("Parking with ID " + droneDto.getParkingId() + " does not exist.")
        );
        Drone drone = modelMapper.map(droneDto, Drone.class);
        drone.setParking(parking);
        return _droneRepository.save(drone);
    }

    public Drone updateDrone(Long id, DroneDTO droneDto){
        Drone drone = _droneRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Drone with ID " + id + " does not exist.")
        );

        Parking parking = _parkingRepository.findById(droneDto.getParkingId()).orElseThrow(
                () -> new IllegalArgumentException("Parking with ID " + droneDto.getParkingId() + " does not exist.")
        );

        drone.setName(droneDto.getName());
        drone.setModel(droneDto.getModel());
        drone.setSerialNumber(droneDto.getSerialNumber());
        drone.setParking(parking);

        return _droneRepository.save(drone);
    }

    public void deleteDrone(Long id) {
        _droneRepository.deleteById(id);
    }

}
