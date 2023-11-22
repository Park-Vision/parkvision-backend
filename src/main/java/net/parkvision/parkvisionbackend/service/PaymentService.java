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
    private final PaymentRepository _paymentRepository;
    private final UserRepository _userRepository;

    @Value("${stripe.key.publishable}")
    private String stripeKey;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository) {
        this._paymentRepository = paymentRepository;
        this._userRepository = userRepository;
    }

    public List<Payment> getAllPayments() {
        return _paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return _paymentRepository.findById(id);
    }

    public Payment createPayment(Payment payment) {
        if (!_userRepository.existsById(payment.getUser().getId())) {
            throw new IllegalArgumentException("User with ID " + payment.getUser().getId() + " does not exist.");
        }
        Stripe.apiKey = stripeKey;
        User user = _userRepository.getReferenceById(payment.getUser().getId());

        Map<String, Object> card = new HashMap<>();
        card.put("number", payment.getCardNumber());
        card.put("exp_month", payment.getExpMonth());
        card.put("exp_year", payment.getExpYear());
        card.put("cvc", payment.getCvc());

        Map<String, Object> params = new HashMap<>();
        params.put("card", card);
        payment.setUser(user);

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
        _paymentRepository.save(payment);
        return payment;
    }

    public Payment updatePayment(Payment payment) {
        if (!_paymentRepository.existsById(payment.getId())) {
            throw new IllegalArgumentException("Payment with ID " + payment.getId() + " does not exist.");
        }
        return _paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        _paymentRepository.deleteById(id);
    }


}
