package net.parkvision.parkvisionbackend.dto;

import lombok.Data;
import java.time.*;

@Data
public class ParkingDTO {
    private Long id;
    private String name;
    private String description;
    private String city;
    private String street;
    private String zipCode;
    private double costRate;
    private OffsetTime startTime;
    private OffsetTime endTime;
    private double latitude;
    private double longitude;
    private ZoneOffset timeZone;
    private String currency;
}
