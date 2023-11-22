package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "parkingSpot" })
@Entity
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double latitude;
    private double longitude;
    @ManyToOne(fetch = FetchType.LAZY)
    private ParkingSpot parkingSpot;
}
