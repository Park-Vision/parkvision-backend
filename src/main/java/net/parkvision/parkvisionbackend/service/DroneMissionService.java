package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.DroneMission;
import net.parkvision.parkvisionbackend.repository.DroneMissionRepository;
import net.parkvision.parkvisionbackend.repository.DroneRepository;
import net.parkvision.parkvisionbackend.repository.MissionSpotResultRepository;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DroneMissionService {

    private final DroneMissionRepository droneMissionRepository;
    private final ParkingRepository parkingRepository;
    private final DroneRepository droneRepository;
    private final MissionSpotResultRepository missionSpotResultRepository;

    @Autowired
    public DroneMissionService(DroneMissionRepository droneMissionRepository, ParkingRepository parkingRepository,
                               DroneRepository droneRepository,
                               MissionSpotResultRepository missionSpotResultRepository) {
        this.droneMissionRepository = droneMissionRepository;
        this.parkingRepository = parkingRepository;
        this.droneRepository = droneRepository;
        this.missionSpotResultRepository = missionSpotResultRepository;
    }

    public List<DroneMission> getAllDroneMissions() {
        return droneMissionRepository.findAll();
    }

    public Optional<DroneMission> getDroneMissionById(Long id) {
        return droneMissionRepository.findById(id);
    }

    public DroneMission createDroneMission(DroneMission droneMission) {
        if (!parkingRepository.existsById(droneMission.getParking().getId())) {
            throw new IllegalArgumentException("Parking with ID " + droneMission.getParking().getId() + " does not " +
                    "exist.");
        }

        if (!droneRepository.existsById(droneMission.getDrone().getId())) {
            throw new IllegalArgumentException("Drone with ID " + droneMission.getDrone().getId() + " does not exist.");
        }

        DroneMission droneMissionResult = droneMissionRepository.save(droneMission);

        for (int i = 0; i < droneMission.getMissionSpotResultList().size(); i++) {
            droneMission.getMissionSpotResultList().get(i).setDroneMission(droneMission);
            missionSpotResultRepository.save(droneMission.getMissionSpotResultList().get(i));
        }
        return droneMissionResult;
    }

    public DroneMission updateDroneMission(DroneMission droneMission) {

        if (!droneMissionRepository.existsById(droneMission.getId())) {
            throw new IllegalArgumentException("DroneMission with ID " + droneMission.getId() + " does not exist.");
        }

        if (!parkingRepository.existsById(droneMission.getParking().getId())) {
            throw new IllegalArgumentException("Parking with ID " + droneMission.getParking().getId() + " does not " +
                    "exist.");
        }

        if (!droneRepository.existsById(droneMission.getDrone().getId())) {
            throw new IllegalArgumentException("Drone with ID " + droneMission.getDrone().getId() + " does not exist.");
        }

        droneMission.setMissionSpotResultList(droneMission.getMissionSpotResultList());

        for (int i = 0; i < droneMission.getMissionSpotResultList().size(); i++) {
            droneMission.getMissionSpotResultList().get(i).setDroneMission(droneMission);
            missionSpotResultRepository.save(droneMission.getMissionSpotResultList().get(i));
        }
        droneMission.setStatus(droneMission.getStatus());
        droneMission.setMissionStartDate(droneMission.getMissionStartDate());
        droneMission.setMissionEndDate(droneMission.getMissionEndDate());

        return droneMissionRepository.save(droneMission);
    }

    public void deleteDroneMission(Long id) {
        droneMissionRepository.deleteById(id);
    }
}
