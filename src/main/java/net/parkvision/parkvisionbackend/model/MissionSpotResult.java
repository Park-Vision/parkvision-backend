package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MissionSpotResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean occupied;

    @ManyToOne
    private ParkingSpot parkingSpot;
    @ManyToOne(cascade = CascadeType.ALL)
    private DroneMission droneMission;
}
