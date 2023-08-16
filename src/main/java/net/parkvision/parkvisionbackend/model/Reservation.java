package net.parkvision.parkvisionbackend.model;

import java.util.Date;

import lombok.Data;

@Data
public class Reservation {
    private Long id;
    private String plateNumber;
    private Date startDate;
    private Date endDate;

    private ParkingSpot parkingSpot; // Many-to-one relationship
}
