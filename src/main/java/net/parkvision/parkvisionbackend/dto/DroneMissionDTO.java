package net.parkvision.parkvisionbackend.dto;

import lombok.Data;
import java.util.Date;

@Data
public class DroneMissionDTO {
    private Long id;
    private String missionName;
    private String missionDescription;
    private String missionStatus;
    private Date missionStartDate;
    private Date missionEndDate;
    private Long parkingId;
    private Long droneId;
}

