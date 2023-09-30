package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DroneMission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String missionName;
    private String missionDescription;
    private String missionStatus;
    private Date missionStartDate;
    private Date missionEndDate;

    @ManyToOne
    private Parking parking; // Many-to-one relationship
    @ManyToOne
    private Drone drone; // Many-to-one relationship
}
