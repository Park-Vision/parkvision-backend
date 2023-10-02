package net.parkvision.parkvisionbackend.model;

import java.util.List;

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
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String address;
    private double costRate;
    private String openHours;

    @OneToMany
    private List<ParkingModerator> parkingModerator;

    @OneToMany
    private List<ParkingSpot> parkingSpots; // One-to-many relationship
    @OneToMany
    private List<DroneMission> droneMissions; // One-to-many relationship
    @OneToMany
    private List<Drone> drones; // One-to-many relationship
}
