package net.parkvision.parkvisionbackend.model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date startDate;
    private Date endDate;
    private String registrationNumber;

    @ManyToOne
    private Client client; // Many-to-one relationship
    @ManyToOne
    private Car car; // Many-to-one relationship
    @ManyToOne
    private ParkingSpot parkingSpot; // Many-to-one relationship
    @OneToOne
    private Payment payment; // One-to-one relationship

}
