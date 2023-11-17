package net.parkvision.parkvisionbackend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StripeCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private Double amount;
    private String currency;
    private Boolean success;
    private String message;
    private String chargeId;
    @ManyToOne
    private Payment payment;
    @ManyToOne
    private Reservation reservation;

}
