package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Card number is required")
    @Size(min = 12, max = 19, message = "Card number must be 16 digits")
    private String cardNumber;

    @NotNull(message = "Expiration month is required")
    @Size(min = 2, max = 2, message = "Expiration month must be 2 digits")
    private String expMonth;

    @NotNull(message = "Expiration year is required")
    @Size(min = 4, max = 4, message = "Expiration year must be 4 digits")
    private String expYear;

    @NotNull(message = "CVC is required")
    @Size(min = 3, max = 4, message = "CVC must be between 3 and 4 digits")
    private String cvc;

    private String token;

    @OneToOne
    private Reservation reservation;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.REMOVE)
    private StripeCharge stripeCharge;
}
