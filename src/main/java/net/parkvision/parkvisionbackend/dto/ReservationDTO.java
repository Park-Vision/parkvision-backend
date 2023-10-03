package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReservationDTO {
    private Long id;
    private Date startDate;
    private Date endDate;
    private String registrationNumber;

    private Long clientId;
    private Long carId;
    private Long parkingSpotId;
}
