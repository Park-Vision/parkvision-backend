package net.parkvision.parkvisionbackend.model;

import com.stripe.param.TopupListParams;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.parkvision.parkvisionbackend.dto.ReservationDTO;

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
    private String cardNumber;
    private String expMonth;
    private String expYear;
    private String cvc;
    private String username;
    private boolean success;
    private String token;
}
