package net.parkvision.parkvisionbackend.model;

import java.util.List;

import lombok.Data;

@Data
public class Parking {
    private Long id;
    private String name;
    private String description;
    private String address;
    private double costRate;
    private String openHours;

    private List<ParkingSpot> parkingSpots; // One-to-many relationship
}
