package net.parkvision.parkvisionbackend.model;

import lombok.Data;

@Data
public class ParkingSpot {
    private Long id;
    private String spotNumber;
    private boolean occupied;
    private String vehicleLicensePlate;

    private Parking parking; // Many-to-one relationship
}
