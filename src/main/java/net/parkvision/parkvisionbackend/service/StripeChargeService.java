package net.parkvision.parkvisionbackend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import net.parkvision.parkvisionbackend.model.Payment;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.model.StripeCharge;
import net.parkvision.parkvisionbackend.repository.PaymentRepository;
import net.parkvision.parkvisionbackend.repository.ReservationRepository;
import net.parkvision.parkvisionbackend.repository.StripeChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StripeChargeService {
    private final StripeChargeRepository _stripeChargeRepository;
    private final PaymentRepository _paymentRepository;
    private final ReservationRepository _reservationRepository;

    @Value("${stripe.key.secret}")
    private String stripeKey;

    @Autowired
    public StripeChargeService(StripeChargeRepository stripeChargeRepository, PaymentRepository paymentRepository,
                               ReservationRepository reservationRepository) {
        this._stripeChargeRepository = stripeChargeRepository;
        this._paymentRepository = paymentRepository;
        this._reservationRepository = reservationRepository;
    }

    public List<StripeCharge> getAllStripeCharges() {
        return _stripeChargeRepository.findAll();
    }

    public Optional<StripeCharge> getStripeChargeById(Long id) {
        return _stripeChargeRepository.findById(id);
    }

    public StripeCharge createStripeCharge(StripeCharge stripeCharge) {
        Stripe.apiKey = stripeKey;
        if (!_paymentRepository.existsById(stripeCharge.getPayment().getId())) {
            throw new IllegalArgumentException("Payment with ID " + stripeCharge.getPayment().getId() + " does not " +
                    "exist.");
        }
        if (!_reservationRepository.existsById(stripeCharge.getReservation().getId())) {
            throw new IllegalArgumentException("Reservation with ID " + stripeCharge.getReservation().getId() + " " +
                    "does not exist.");
        }
        stripeCharge.setSuccess(false);
        Payment payment = _paymentRepository.getReferenceById(stripeCharge.getPayment().getId());
        Reservation reservation = _reservationRepository.getReferenceById(stripeCharge.getReservation().getId());
        stripeCharge.setPayment(payment);

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int) (stripeCharge.getAmount() * 100));
        chargeParams.put("currency", stripeCharge.getCurrency());
        chargeParams.put("source", stripeCharge.getPayment().getToken());

        Map<String, Object> metaData = new HashMap<>();
        metaData.put("id", stripeCharge.getPayment().getToken());
        chargeParams.put("metadata", metaData);

        try {
            Charge charge = Charge.create(chargeParams);
            stripeCharge.setMessage(charge.getOutcome().getSellerMessage());
            if (charge.getPaid()) {
                stripeCharge.setReservation(reservation);
                stripeCharge.setSuccess(true);
            }
            _stripeChargeRepository.save(stripeCharge);
            return stripeCharge;
        } catch (StripeException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public void deleteStripeCharge(Long id) {
        _stripeChargeRepository.deleteById(id);
    }
}
