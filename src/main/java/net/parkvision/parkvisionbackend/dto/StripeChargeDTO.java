package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

@Data
public class StripeChargeDTO {
    private String id;
    private String username;
    private Double amount;
    private String currency;
    private Boolean success;
    private String message;
    private PaymentDTO payment;
    private ReservationDTO reservation;
}
