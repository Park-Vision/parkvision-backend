package net.parkvision.parkvisionbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class ParkingDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private double costRate;
    private String openHours;
}
