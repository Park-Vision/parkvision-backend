package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.dto.ReservationDTO;
import net.parkvision.parkvisionbackend.model.Car;
import net.parkvision.parkvisionbackend.model.Client;
import net.parkvision.parkvisionbackend.model.ParkingSpot;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.repository.CarRepository;
import net.parkvision.parkvisionbackend.repository.ClientRepository;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import net.parkvision.parkvisionbackend.repository.ReservationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository _reservationRepository;
    private final ClientRepository _clientRepository;
    private final CarRepository _carRepository;
    private final ParkingSpotRepository _parkingSpotRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, ClientRepository clientRepository, CarRepository carRepository, ParkingSpotRepository parkingSpotRepository, ModelMapper modelMapper) {
        _reservationRepository = reservationRepository;
        _clientRepository = clientRepository;
        _carRepository = carRepository;
        _parkingSpotRepository = parkingSpotRepository;
        this.modelMapper = modelMapper;
    }

    public List<Reservation> getAllReservations() {
        return _reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return _reservationRepository.findById(id);
    }

    public Reservation createReservation(ReservationDTO reservationDto) {
        Car car = null;
        Client client = _clientRepository.findById(reservationDto.getClientId()).orElseThrow(
                () -> new IllegalArgumentException("Client with ID " + reservationDto.getClientId() + " does not exist.")
        );

        ParkingSpot parkingSpot = _parkingSpotRepository.findById(reservationDto.getParkingSpotId()).orElseThrow(
                () -> new IllegalArgumentException("ParkingSpot with ID " + reservationDto.getParkingSpotId() + " does not exist.")
        );

        if(reservationDto.getCarId() != null) {
            car = _carRepository.findById(reservationDto.getCarId()).orElseThrow(
                    () -> new IllegalArgumentException("Car with ID " + reservationDto.getCarId() + " does not exist.")
            );
        }

        Reservation reservation = modelMapper.map(reservationDto, Reservation.class);

        reservation.setClient(client);
        reservation.setParkingSpot(parkingSpot);
        if (car != null) {
            reservation.setCar(car);
        }
        return _reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Long id, ReservationDTO reservationDto){
        Reservation reservation = _reservationRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Reservation with ID " + id + " does not exist.")
        );

        Car car = null;

        Client client = _clientRepository.findById(reservationDto.getClientId()).orElseThrow(
                () -> new IllegalArgumentException("Client with ID " + reservationDto.getClientId() + " does not exist.")
        );

        ParkingSpot parkingSpot = _parkingSpotRepository.findById(reservationDto.getParkingSpotId()).orElseThrow(
                () -> new IllegalArgumentException("ParkingSpot with ID " + reservationDto.getParkingSpotId() + " does not exist.")
        );

        if(reservationDto.getCarId() != null) {
            car = _carRepository.findById(reservationDto.getCarId()).orElseThrow(
                    () -> new IllegalArgumentException("Car with ID " + reservationDto.getCarId() + " does not exist.")
            );
        }

        reservation.setStartDate(reservationDto.getStartDate());
        reservation.setEndDate(reservationDto.getEndDate());
        reservation.setRegistrationNumber(reservationDto.getRegistrationNumber());
        reservation.setClient(client);
        reservation.setParkingSpot(parkingSpot);
        if (car != null) {
            reservation.setCar(car);
        }

        return _reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        _reservationRepository.deleteById(id);
    }
}
