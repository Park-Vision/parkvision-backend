package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

@Data
public class StripeChargeDTO {
    private Long id;
    private String stripeToken;
    private String username;
    private Double amount;
    private String currency;
    private Boolean success;
    private String message;
    private PaymentDTO payment;
}
