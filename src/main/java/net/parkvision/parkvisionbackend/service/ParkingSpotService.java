package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingSpotService {

    private final ParkingSpotRepository _parkingSpotRepository;

    @Autowired
    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository) {
        this._parkingSpotRepository = parkingSpotRepository;
    }

    // generate all crud methods

    public List<ParkingSpot> getAllParkingSpots() {
        return _parkingSpotRepository.findAll();
    }

    public Optional<ParkingSpot> getParkingSpotById(Long id) {
        return _parkingSpotRepository.findById(id);
    }

    public ParkingSpot createParkingSpot(ParkingSpot parkingSpot) {
        return _parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot updateParkingSpot(Long id, ParkingSpot parkingSpot){
        if (_parkingSpotRepository.existsById(id)) {
            parkingSpot.setId(id);
            return _parkingSpotRepository.save(parkingSpot);
        } else {
            throw new IllegalArgumentException("ParkingSpot with ID " + id + " does not exist.");
        }
    }

    // so we will use soft delete instead of hard delete
    public void softDeleteParkingSpot(Long id) {
        _parkingSpotRepository.findById(id).ifPresent(parkingSpot -> {
            parkingSpot.setActive(false);
            _parkingSpotRepository.save(parkingSpot);
        });
    }

    public void hardDeleteParkingSpot(Long id) {
        _parkingSpotRepository.deleteById(id);
    }

}
