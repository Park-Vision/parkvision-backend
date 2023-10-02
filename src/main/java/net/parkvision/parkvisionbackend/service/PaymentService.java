package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.Payment;
import net.parkvision.parkvisionbackend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository _paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this._paymentRepository = paymentRepository;
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

    public Payment updatePayment(Long id, Payment payment){
        if (_paymentRepository.existsById(id)) {
            payment.setId(id);
            return _paymentRepository.save(payment);
        } else {
            throw new IllegalArgumentException("Payment with ID " + id + " does not exist.");
        }
    }

    public void deletePayment(Long id) {
        _paymentRepository.deleteById(id);
    }

}
