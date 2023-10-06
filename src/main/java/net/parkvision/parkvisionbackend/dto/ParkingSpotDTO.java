package net.parkvision.parkvisionbackend.dto;

import lombok.Data;


@Data
public class ParkingSpotDTO {
    private Long id;
    private String spotNumber;
    private boolean occupied;
    private boolean active;
    private ParkingDTO parkingDTO;
}
