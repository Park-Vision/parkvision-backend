package net.parkvision.parkvisionbackend.dto;

import lombok.Data;
import net.parkvision.parkvisionbackend.model.Point;

import java.util.List;

@Data
public class ParkingSpotDTO {
    private Long id;
    private String spotNumber;
    private boolean occupied;
    private boolean active;
    private List<Point> points; // Assuming you want to include a list of PointDTO objects
    private Long parkingId; // Assuming you want to include the parking's ID in the DTO
    private List<Long> reservationIds; // Assuming you want to include the IDs of reservations in the DTO
}
