package net.parkvision.parkvisionbackend.model;

import java.time.*;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "City is required")
    private String city;

    @NotNull(message = "Street is required")
    private String street;

    @NotNull(message = "Zip code is required")
    private String zipCode;

    @NotNull(message = "Cost rate is required")
    private double costRate;

    @NotNull(message = "Start time is required")
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetTime startTime;

    @NotNull(message = "End time is required")
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetTime endTime;

    @NotNull(message = "Latitude is required")
    private double latitude;

    @NotNull(message = "Longitude is required")
    private double longitude;

    @NotNull(message = "Time zone is required")
    private ZoneOffset timeZone;

    @NotNull(message = "Currency is required")
    private String currency;

    @OneToMany(mappedBy = "parking", cascade = CascadeType.REMOVE)
    private List<ParkingManager> parkingManagers;

    @OneToMany(mappedBy = "parking", cascade = CascadeType.REMOVE)
    private List<ParkingSpot> parkingSpots;

    @OneToMany(mappedBy = "parking", cascade = CascadeType.REMOVE)
    private List<DroneMission> droneMissions;

    @OneToMany(mappedBy = "parking", cascade = CascadeType.REMOVE)
    private List<Drone> drones;
}
