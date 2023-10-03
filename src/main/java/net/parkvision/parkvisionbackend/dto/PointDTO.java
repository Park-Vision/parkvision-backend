package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

@Data
public class PointDTO {
    private Long id;
    private double latitude;
    private double longitude;
    private Long parkingSpotId;
}
