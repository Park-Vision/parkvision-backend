package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String spotNumber;
    private boolean occupied;
    private boolean active;
    // each parking spot has 5 points that ara geographic coordinates store it as a list
    @OneToMany
    private List<Point> points;

    @OneToMany
    private List<Reservation> reservations; // One-to-many relationship
    @ManyToOne
    private Parking parking; // Many-to-one relationship

}
