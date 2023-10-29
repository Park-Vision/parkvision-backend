package net.parkvision.parkvisionbackend.model;

import java.time.ZonedDateTime;
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
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private String registrationNumber;

    @ManyToOne
    private User user;
    @ManyToOne
    private ParkingSpot parkingSpot;
    @OneToMany
    private List<Payment> payment;
}
