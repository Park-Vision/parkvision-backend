package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository _reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this._reservationRepository = reservationRepository;
    }

    public List<Reservation> getAllReservations() {
        return _reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return _reservationRepository.findById(id);
    }

    public Reservation createReservation(Reservation reservation) {
        return _reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Long id, Reservation reservation){
        if (_reservationRepository.existsById(id)) {
            reservation.setId(id);
            return _reservationRepository.save(reservation);
        } else {
            throw new IllegalArgumentException("Reservation with ID " + id + " does not exist.");
        }
    }

    public void deleteReservation(Long id) {
        _reservationRepository.deleteById(id);
    }
}
