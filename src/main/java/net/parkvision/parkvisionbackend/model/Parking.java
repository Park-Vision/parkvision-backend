package net.parkvision.parkvisionbackend.model;

import java.time.*;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

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
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetTime startTime;
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetTime endTime;
    private double latitude;
    private double longitude;
    private ZoneOffset timeZone;
    private String currency;

    @OneToMany(mappedBy = "parking")
    private List<ParkingManager> parkingManager;

    @OneToMany(mappedBy = "parking")
    private List<ParkingSpot> parkingSpots;
    @OneToMany(mappedBy = "parking")
    private List<DroneMission> droneMissions;
    @OneToMany(mappedBy = "parking")
    private List<Drone> drones;
}
