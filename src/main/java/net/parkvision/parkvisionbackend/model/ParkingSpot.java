package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String spotNumber;
    private boolean occupied;

    private Reservation reservation; // One-to-one relationship
    private Parking parking; // Many-to-one relationship
}
