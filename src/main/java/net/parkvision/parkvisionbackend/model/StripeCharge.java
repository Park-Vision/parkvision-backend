package net.parkvision.parkvisionbackend.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StripeCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required")
    private Double amount;

    @NotNull(message = "Currency is required")
    private String currency;

    @NotNull(message = "Source is required")
    private Boolean success;

    @NotNull(message = "Source is required")
    private String message;

    private String chargeId;
    @OneToOne
    @NotNull(message = "Payment is required")
    private Payment payment;
}
