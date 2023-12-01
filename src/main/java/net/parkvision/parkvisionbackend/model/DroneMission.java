package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Mission start date is required")
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetDateTime missionStartDate;

    @NotNull(message = "Mission end date is required")
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetDateTime missionEndDate;

    @NotNull(message = "Mission status is required")
    private String status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "droneMission")
    private List<MissionSpotResult> missionSpotResultList;
    @ManyToOne
    @NotNull(message = "Parking is required")
    private Parking parking;

    @ManyToOne
    @NotNull(message = "Drone is required")
    private Drone drone;
}
