package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class StripeChargeService {
    private final PaymentRepository _paymentRepository;

    public StripeChargeService(PaymentRepository paymentRepository) {
        _paymentRepository = paymentRepository;
    }
}
