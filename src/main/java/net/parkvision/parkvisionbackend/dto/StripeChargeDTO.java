package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

@Data
public class StripeChargeDTO {
    private Long id;
    private String username;
    private Double amount;
    private String currency;
    private Boolean success;
    private String message;
    private String chargeId;
    private PaymentDTO payment;
    private ReservationDTO reservation;
}
