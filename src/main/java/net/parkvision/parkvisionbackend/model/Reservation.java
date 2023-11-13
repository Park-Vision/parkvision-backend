package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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

    @ManyToOne
    private User user;
    @ManyToOne
    private ParkingSpot parkingSpot;
    @OneToMany
    private List<Payment> payment;
}
