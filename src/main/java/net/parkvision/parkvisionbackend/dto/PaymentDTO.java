package net.parkvision.parkvisionbackend.dto;

import lombok.Data;
import net.parkvision.parkvisionbackend.model.PaymentStatus;
import net.parkvision.parkvisionbackend.model.Reservation;

@Data
public class PaymentDTO {
    private Long id;
    private ReservationDTO reservation;
    private String cardNumber;
    private String expMonth;
    private String expYear;
    private String cvc;
    private boolean success;
    private String token;

}
