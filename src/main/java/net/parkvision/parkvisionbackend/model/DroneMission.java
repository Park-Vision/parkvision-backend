package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
public class DroneMission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String missionName;
    private String missionDescription;
    private String missionStatus;
    private Date missionStartDate;
    private Date missionEndDate;

    private Parking parking; // Many-to-one relationship
    private Drone drone; // Many-to-one relationship
}
