package net.parkvision.parkvisionbackend.model;

import java.util.Date;
import java.util.List;

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
    private User user; // Many-to-one relationship
    @ManyToOne
    private ParkingSpot parkingSpot; // Many-to-one relationship
    @OneToMany
    private List<Payment> payment; // One-to-many relationship

}
