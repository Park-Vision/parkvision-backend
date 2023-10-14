package net.parkvision.parkvisionbackend.model;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private String registrationNumber;

    @ManyToOne
    private User user; // Many-to-one relationship
    @ManyToOne
    private ParkingSpot parkingSpot; // Many-to-one relationship
    @OneToMany
    private List<Payment> payment; // One-to-many relationship
}
