package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Payment;
import net.parkvision.parkvisionbackend.repository.PaymentRepository;
import net.parkvision.parkvisionbackend.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository _paymentRepository;
    private final ReservationRepository _reservationRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        this._paymentRepository = paymentRepository;
        _reservationRepository = reservationRepository;
    }

    public List<Payment> getAllPayments() {
        return _paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return _paymentRepository.findById(id);
    }

    public Payment createPayment(Payment payment) {
        return _paymentRepository.save(payment);
    }

    public Payment updatePayment(Payment payment){
        if(!_paymentRepository.existsById(payment.getId())){
            throw new IllegalArgumentException("Payment with ID " + payment.getId() + " does not exist.");
        }

        if(!_reservationRepository.existsById(payment.getReservation().getId())){
            throw new IllegalArgumentException("Reservation with ID " + payment.getReservation().getId() + " does not exist.");
        }

        payment.setStatus(payment.getStatus());
        payment.setReservation(payment.getReservation());
        return _paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        _paymentRepository.deleteById(id);
    }

}
