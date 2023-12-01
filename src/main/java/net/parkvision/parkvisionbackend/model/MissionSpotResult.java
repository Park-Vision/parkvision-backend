package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Occupied is required")
    private Boolean occupied;

    @ManyToOne
    @NotNull(message = "Parking spot is required")
    private ParkingSpot parkingSpot;

    @ManyToOne(cascade = CascadeType.ALL)
    @NotNull(message = "Drone mission is required")
    private DroneMission droneMission;
}
