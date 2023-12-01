package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"parkingSpot"})
@Entity
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Latitude is required")
    private double latitude;
    @NotNull(message = "Longitude is required")
    private double longitude;
    @ManyToOne
    @NotNull(message = "Parking spot is required")
    private ParkingSpot parkingSpot;
}
