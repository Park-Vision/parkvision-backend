package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.dto.ParkingSpotDTO;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingSpotService {

    private final ParkingSpotRepository _parkingSpotRepository;
    // add ParkingRepository and ModelMapper
    private final ParkingRepository _parkingRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository, ParkingRepository parkingRepository, ModelMapper modelMapper) {
        this._parkingSpotRepository = parkingSpotRepository;
        _parkingRepository = parkingRepository;
        this.modelMapper = modelMapper;
    }


    public List<ParkingSpot> getAllParkingSpots() {
        return _parkingSpotRepository.findAll();
    }

    public Optional<ParkingSpot> getParkingSpotById(Long id) {
        return _parkingSpotRepository.findById(id);
    }

    public ParkingSpot createParkingSpot(ParkingSpotDTO parkingSpotDto) {
        Parking parking = _parkingRepository.findById(parkingSpotDto.getParkingId()).orElseThrow(
                () -> new IllegalArgumentException("Parking with ID " + parkingSpotDto.getParkingId() + " does not exist.")
        );

        ParkingSpot parkingSpot = modelMapper.map(parkingSpotDto, ParkingSpot.class);
        parkingSpot.setParking(parking);

        return _parkingSpotRepository.save(parkingSpot);
    }

    public ParkingSpot updateParkingSpot(Long id, ParkingSpotDTO parkingSpotDto){
        ParkingSpot parkingSpot = _parkingSpotRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("ParkingSpot with ID " + id + " does not exist.")
        );

        Parking parking = _parkingRepository.findById(parkingSpotDto.getParkingId()).orElseThrow(
                () -> new IllegalArgumentException("Parking with ID " + parkingSpotDto.getParkingId() + " does not exist.")
        );

        parkingSpot.setSpotNumber(parkingSpotDto.getSpotNumber());
        parkingSpot.setOccupied(parkingSpotDto.isOccupied());
        parkingSpot.setActive(parkingSpotDto.isActive());

        parkingSpot.setParking(parking);

        return _parkingSpotRepository.save(parkingSpot);
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
