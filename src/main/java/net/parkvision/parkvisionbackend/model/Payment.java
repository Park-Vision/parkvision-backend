package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
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
    @ManyToOne
    private User user;
    @Size(min = 12, max = 19, message = "Card number must be 16 digits")
    private String cardNumber;
    @Size(min = 2, max = 2, message = "Expiration month must be 2 digits")
    private String expMonth;
    @Size(min = 4, max = 4, message = "Expiration year must be 4 digits")
    private String expYear;
    @Size(min = 3, max = 4, message = "CVC must be between 3 and 4 digits")
    private String cvc;
    private String token;
    @ManyToOne
    private Reservation reservation;
}
