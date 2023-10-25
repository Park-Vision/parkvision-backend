package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

import java.sql.Time;

@Data
public class ParkingDTO {
    private Long id;
    private String name;
    private String description;
    private String city;
    private String street;
    private String zipCode;
    private double costRate;
    private Time startTime;
    private Time endTime;
    private double latitude;
    private double longitude;
}
