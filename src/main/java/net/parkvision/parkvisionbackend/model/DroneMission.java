package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DroneMission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetDateTime missionStartDate;
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetDateTime missionEndDate;
    private String status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "droneMission")
    private List<MissionSpotResult> missionSpotResultList;
    @ManyToOne
    private Parking parking; // Many-to-one relationship
    @ManyToOne
    private Drone drone; // Many-to-one relationship
}
