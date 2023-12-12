package net.parkvision.parkvisionbackend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
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
    private final StripeChargeRepository stripeChargeRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;


    @Value("${stripe.key.secret}")
    private String stripeKey;

    @Autowired
    public StripeChargeService(StripeChargeRepository stripeChargeRepository, PaymentRepository paymentRepository,
                               ReservationRepository reservationRepository) {
        this.stripeChargeRepository = stripeChargeRepository;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<StripeCharge> getAllStripeCharges() {
        return stripeChargeRepository.findAll();
    }

    public Optional<StripeCharge> getStripeChargeById(Long id) {
        return stripeChargeRepository.findById(id);
    }

    public Optional<StripeCharge> getStripeChargeByReservationId(Long id) {
        //get payment by reservation id
        Optional<Payment> payment = paymentRepository.findByReservationId(id);

        return stripeChargeRepository.findByPaymentId(payment.get().getId());
    }

    public StripeCharge createStripeCharge(StripeCharge stripeCharge, Long reservationId) {
        Stripe.apiKey = stripeKey;
        if (!paymentRepository.existsById(stripeCharge.getPayment().getId())) {
            throw new IllegalArgumentException("Payment with ID " + stripeCharge.getPayment().getId() + " does not " +
                    "exist.");
        }
        if (!reservationRepository.existsById(reservationId)) {
            throw new IllegalArgumentException("Reservation with ID " + reservationId + " " +
                    "does not exist.");
        }
        stripeCharge.setSuccess(false);
        Payment payment = paymentRepository.getReferenceById(stripeCharge.getPayment().getId());
        Reservation reservation = reservationRepository.getReferenceById(reservationId);
        stripeCharge.setPayment(payment);

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int) (stripeCharge.getAmount() * 100));
        chargeParams.put("currency", stripeCharge.getCurrency());
        chargeParams.put("source", stripeCharge.getPayment().getToken());

        Map<String, Object> metaData = new HashMap<>();
        metaData.put("id", stripeCharge.getPayment().getToken());
        chargeParams.put("metadata", metaData);
        payment.setReservation(reservation);

        try {
            Charge charge = Charge.create(chargeParams);
            stripeCharge.setChargeId(charge.getId());
            stripeCharge.setMessage(charge.getOutcome().getSellerMessage());
            if (charge.getPaid()) {
                stripeCharge.setSuccess(true);
            }
        } catch (StripeException exception) {
            stripeCharge.setMessage(exception.getMessage());
            stripeCharge.setSuccess(false);
        }
        stripeChargeRepository.save(stripeCharge);
        return stripeCharge;
    }

    public StripeCharge refundCharge(Long id) {
        if (!stripeChargeRepository.existsById(id)) {
            throw new IllegalArgumentException("StripeCharge with ID " + id + " " +
                    "does not exist.");
        }
        StripeCharge stripeCharge = stripeChargeRepository.getReferenceById(id);

        if (stripeCharge.getChargeId() != null) {

            try {

                Map<String, Object> params = new HashMap<>();
                params.put(
                        "charge",
                        stripeCharge.getChargeId()
                );

                Refund refund = Refund.create(params);

                if (refund.getStatus().equals("succeeded")) {
                    stripeCharge.setSuccess(refund.getStatus().equals("succeeded"));
                    stripeCharge.setMessage("refund " + refund.getStatus());
                }
                stripeChargeRepository.save(stripeCharge);

                return stripeCharge;
            } catch (StripeException e) {
                throw new RuntimeException("Error processing refund: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("StripeCharge with ID " + id + " not found.");
        }
    }

    public StripeCharge updateStripeCharge(StripeCharge stripeCharge) {
        if (!stripeChargeRepository.existsById(stripeCharge.getId())) {
            throw new IllegalArgumentException("StripeCharge with ID " + stripeCharge.getId() + " does not exist.");
        }
        return stripeChargeRepository.save(stripeCharge);
    }

    public void deleteStripeCharge(Long id) {

        stripeChargeRepository.deleteById(id);
    }

}
