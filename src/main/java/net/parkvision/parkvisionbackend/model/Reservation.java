package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetDateTime startDate;
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetDateTime endDate;
    private String registrationNumber;
    private double amount;
    @ManyToOne
    @NotNull(message = "User is required")
    private User user;
    @ManyToOne
    @NotNull(message = "Parking spot is required")
    private ParkingSpot parkingSpot;
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.REMOVE)
    private List<Payment> payment;
}
