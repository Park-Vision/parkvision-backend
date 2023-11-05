package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private Long id;
    private UserDTO user;
    private String cardNumber;
    private String expMonth;
    private String expYear;
    private String cvc;
    private boolean success;
    private String token;

}
