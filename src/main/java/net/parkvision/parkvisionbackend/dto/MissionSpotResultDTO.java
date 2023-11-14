package net.parkvision.parkvisionbackend.dto;


import lombok.Data;

@Data
public class MissionSpotResultDTO {
    private Long id;
    private Boolean occupied;
    private ParkingSpotDTO parkingSpotDTO;
}
