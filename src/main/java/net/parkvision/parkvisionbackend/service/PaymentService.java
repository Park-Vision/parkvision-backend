package net.parkvision.parkvisionbackend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;
import net.parkvision.parkvisionbackend.model.Payment;
import net.parkvision.parkvisionbackend.model.Reservation;
import net.parkvision.parkvisionbackend.repository.PaymentRepository;
import net.parkvision.parkvisionbackend.repository.ReservationRepository;
import net.parkvision.parkvisionbackend.repository.StripeChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository _paymentRepository;
    private final ReservationRepository _reservationRepository;
    private final StripeChargeRepository _stripeChargeRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository, StripeChargeRepository stripeChargeRepository) {
        this._paymentRepository = paymentRepository;
        this._reservationRepository = reservationRepository;
        this._stripeChargeRepository = stripeChargeRepository;


    }

    public List<Payment> getAllPayments() {
        return _paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return _paymentRepository.findById(id);
    }

    public Payment createPayment(Payment payment) {
        Stripe.apiKey = "pk_test_51O7dhAIrZoSsqF8FSsocK5PN6flQu4RqAA4h6iU5VMXv2BPBelaOBgKESYUTsJAZZXXOFh5982g9YbK4Lf5I5UIw00m4QsipIP";

        try {
            Map<String, Object> card = new HashMap<>();
            card.put("number", payment.getCardNumber());
            card.put("exp_month", payment.getExpMonth());
            card.put("exp_year", payment.getExpYear());
            card.put("cvc", payment.getCvc());
            Map<String, Object> params = new HashMap<>();
            params.put("card", card);
            Token token = Token.create(params);
            if (token != null && token.getId() != null){
                payment.setSuccess(true);
                payment.setToken(token.getId());
                Reservation reservation = _reservationRepository.getReferenceById(payment.getReservation().getId());
                payment.setReservation(reservation);
            }
            _paymentRepository.save(payment);
            return payment;
        } catch (StripeException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

//    public StripeCharge charge(StripeCharge charge){
//        Stripe.apiKey = "sk_test_51O7dhAIrZoSsqF8F3LIcn6BlsxTxFk4n9PftCY9WFwuULCEtnGja8oPTI8YwT0fGSY9mu82hLlqJMEdyqOxsXroK002KIRuft5";
//
//        try {
//            charge.setSuccess(false);
//            Map<String, Object> chargeParams = new HashMap<>();
//            chargeParams.put("amount", (int) (charge.getAmount() * 100));
//            chargeParams.put("currency", charge.getCurrency());
//            chargeParams.put("source", charge.getStripeToken());
//            Map<String, Object> metaData = new HashMap<>();
//            metaData.put("id", charge.getChargeId());
//            chargeParams.put("metadata", metaData);
//            Charge stripeCharge = Charge.create(chargeParams);
//            charge.setMessage(stripeCharge.getOutcome().getSellerMessage());
//            if (stripeCharge.getPaid()){
//                charge.setChargeId(String.valueOf(charge.getId()));
//                charge.setSuccess(true);
//            }
//            _stripeChargeRepository.save(charge);
//            return charge;
//        } catch (StripeException exception){
//            throw new RuntimeException(exception.getMessage());
//        }
//    }

    public Payment updatePayment(Payment payment) {
        if (!_paymentRepository.existsById(payment.getId())) {
            throw new IllegalArgumentException("Payment with ID " + payment.getId() + " does not exist.");
        }

        if (!_reservationRepository.existsById(payment.getReservation().getId())) {
            throw new IllegalArgumentException("Reservation with ID " + payment.getReservation() + " does not" +
                    " exist.");
        }

        payment.setReservation(payment.getReservation());
        return _paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        _paymentRepository.deleteById(id);
    }



}
