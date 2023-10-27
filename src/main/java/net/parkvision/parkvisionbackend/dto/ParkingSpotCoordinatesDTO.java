package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

@Data
public class ParkingSpotCoordinatesDTO {
    Long parkingSpotId;
    double centerLatitude;
    double centerLongitude;
}
