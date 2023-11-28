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
    private boolean active;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parkingSpot")
    private List<Point> points;

    @OneToMany(mappedBy = "parkingSpot", cascade = CascadeType.REMOVE)
    private List<Reservation> reservations;
    @ManyToOne
    private Parking parking; // Many-to-one relationship
}
