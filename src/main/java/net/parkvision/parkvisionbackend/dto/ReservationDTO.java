package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ReservationDTO {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String registrationNumber;
    private UserDTO userDTO;
    private ParkingSpotDTO parkingSpotDTO;
}
