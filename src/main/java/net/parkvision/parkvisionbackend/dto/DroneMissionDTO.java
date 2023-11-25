package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class DroneMissionDTO {
    private Long id;
    private String status;
    private OffsetDateTime missionStartDate;
    private OffsetDateTime missionEndDate;
    private ParkingDTO parkingDTO;
    private DroneDTO droneDTO;
    private List<MissionSpotResultDTO> missionSpotResultList;
}

