package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.DroneMission;
import net.parkvision.parkvisionbackend.repository.DroneMissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DroneMissionService {

    private final DroneMissionRepository _droneMissionRepository;

    @Autowired
    public DroneMissionService(DroneMissionRepository droneMissionRepository) {
        this._droneMissionRepository = droneMissionRepository;
    }

    public List<DroneMission> getAllDroneMissions() {
        return _droneMissionRepository.findAll();
    }

    public Optional<DroneMission> getDroneMissionById(Long id) {
        return _droneMissionRepository.findById(id);
    }

    public DroneMission createDroneMission(DroneMission droneMission) {
        return _droneMissionRepository.save(droneMission);
    }

    //update droneMission
    public DroneMission updateDroneMission(Long id, DroneMission droneMission){
        if (_droneMissionRepository.existsById(id)) {
            droneMission.setId(id);
            return _droneMissionRepository.save(droneMission);
        } else {
            throw new IllegalArgumentException("DroneMission with ID " + id + " does not exist.");
        }
    }

    public void deleteDroneMission(Long id) {
        _droneMissionRepository.deleteById(id);
    }
}
