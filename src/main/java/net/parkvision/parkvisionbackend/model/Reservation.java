package net.parkvision.parkvisionbackend.model;

import java.util.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date startDate;
    private Date endDate;

    private Client client; // Many-to-one relationship
    private Car car; // Many-to-one relationship
    private ParkingSpot parkingSpot; // Many-to-one relationship
}
