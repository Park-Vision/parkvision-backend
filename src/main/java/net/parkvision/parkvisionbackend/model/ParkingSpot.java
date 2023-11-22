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
    @OneToMany(cascade = CascadeType.ALL)
    private List<Point> points;
    @OneToMany
    private List<Reservation> reservations;
    @ManyToOne
    private Parking parking;
}
