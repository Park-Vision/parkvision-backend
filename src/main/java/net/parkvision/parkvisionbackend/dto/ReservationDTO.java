package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
public class ReservationDTO {
    private Long id;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private String registrationNumber;
    private UserDTO userDTO;
    private ParkingSpotDTO parkingSpotDTO;
}
