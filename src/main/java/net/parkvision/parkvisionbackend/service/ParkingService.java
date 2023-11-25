package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {

    private final ParkingRepository parkingRepository;

    @Autowired
    public ParkingService(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    public List<Parking> getAllParkings() {
        return parkingRepository.findAll();
    }

    public Optional<Parking> getParkingById(Long id) {
        return parkingRepository.findById(id);
    }

    public Parking createParking(Parking parking) {
        return parkingRepository.save(parking);
    }

    public Parking updateParking(Parking parking) {
        if (!parkingRepository.existsById(parking.getId())) {
            throw new IllegalArgumentException("Parking with ID " + parking.getId() + " does not exist.");
        }

        return parkingRepository.save(parking);
    }

    public void deleteParking(Long id) {
        parkingRepository.deleteById(id);
    }
}
