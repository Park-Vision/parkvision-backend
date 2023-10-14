package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Date;

@Data
public class ReservationDTO {
    private Long id;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private String registrationNumber;
    private UserDTO userDTO;
    private ParkingSpotDTO parkingSpotDTO;
}
