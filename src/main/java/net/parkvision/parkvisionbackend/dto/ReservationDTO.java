package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ReservationDTO {
    private Long id;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String registrationNumber;
    private UserDTO userDTO;
    private ParkingSpotDTO parkingSpotDTO;
}
