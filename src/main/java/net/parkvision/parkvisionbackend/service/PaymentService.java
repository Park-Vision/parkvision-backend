package net.parkvision.parkvisionbackend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;
import net.parkvision.parkvisionbackend.model.Payment;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.PaymentRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Value("${stripe.key.publishable}")
    private String stripeKey;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment createPayment(Payment payment) {
        Stripe.apiKey = stripeKey;

        Map<String, Object> card = new HashMap<>();
        card.put("number", payment.getCardNumber());
        card.put("exp_month", payment.getExpMonth());
        card.put("exp_year", payment.getExpYear());
        card.put("cvc", payment.getCvc());

        Map<String, Object> params = new HashMap<>();
        params.put("card", card);

        try {
            Token token = Token.create(params);
            if (token != null && token.getId() != null) {
                payment.setToken(token.getId());
            }
        } catch (StripeException exception) {
            payment.setToken(null);
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error(exception.getMessage());
        }
        paymentRepository.save(payment);
        return payment;
    }

    public Payment updatePayment(Payment payment) {
        if (!paymentRepository.existsById(payment.getId())) {
            throw new IllegalArgumentException("Payment with ID " + payment.getId() + " does not exist.");
        }
        return paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
