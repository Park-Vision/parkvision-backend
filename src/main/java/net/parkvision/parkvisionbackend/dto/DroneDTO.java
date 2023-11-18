package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

@Data
public class DroneDTO {
    private Long id;
    private String name;
    private String model;
    private String serialNumber;
    private ParkingDTO parkingDTO;
    private String droneKey;
}
