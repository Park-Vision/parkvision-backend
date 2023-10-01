package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {

    private final ParkingRepository _parkingRepository;

    @Autowired
    public ParkingService(ParkingRepository parkingRepository) {
        this._parkingRepository = parkingRepository;
    }

    public List<Parking> getAllParkings() {
        return _parkingRepository.findAll();
    }

    public Optional<Parking> getParkingById(Long id) {
        return _parkingRepository.findById(id);
    }

    public Parking createParking(Parking parking) {
        return _parkingRepository.save(parking);
    }

    public Parking updateParking(Long id, Parking parking){
        if (_parkingRepository.existsById(id)) {
            parking.setId(id);
            return _parkingRepository.save(parking);
        } else {
            throw new IllegalArgumentException("Parking with ID " + id + " does not exist.");
        }
    }

    public void deleteParking(Long id) {
        _parkingRepository.deleteById(id);
    }

}
