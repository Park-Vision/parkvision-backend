package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

@Data
public class ParkingDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private double costRate;
    private String openHours;
    private double latitude;
    private double longitude;
}
