package net.parkvision.parkvisionbackend.model;

import java.time.*;
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
    private String city;
    private String street;
    private String zipCode;
    private double costRate;
    private OffsetTime startTime;
    private OffsetTime endTime;
    private double latitude;
    private double longitude;
    private ZoneOffset timeZone;
    private String currency;

    @OneToMany
    private List<ParkingModerator> parkingModerator;

    @OneToMany
    private List<ParkingSpot> parkingSpots;
    @OneToMany
    private List<DroneMission> droneMissions;
    @OneToMany
    private List<Drone> drones;
}
