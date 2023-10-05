package net.parkvision.parkvisionbackend.dto;

import lombok.Data;
import net.parkvision.parkvisionbackend.model.PaymentStatus;

@Data
public class PaymentDTO {
    private Long id;
    private PaymentStatus status;
    private ReservationDTO reservation;
}
