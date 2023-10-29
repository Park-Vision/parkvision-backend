package net.parkvision.parkvisionbackend.model;

import java.sql.Time;
import java.util.List;
import java.util.TimeZone;

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
    private String city;
    private String street;
    private String zipCode;
    private double costRate;
    private Time startTime;
    private Time endTime;
    private double latitude;
    private double longitude;
    private TimeZone timeZone;

    @OneToMany
    private List<ParkingModerator> parkingModerator;

    @OneToMany
    private List<ParkingSpot> parkingSpots;
    @OneToMany
    private List<DroneMission> droneMissions;
    @OneToMany
    private List<Drone> drones;
}
