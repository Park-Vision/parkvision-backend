package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Spot number is required")
    private String spotNumber;

    @NotNull(message = "Active is required")
    private boolean active;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parkingSpot")
    @Size(max = 4, message = "Parking spot must have maximum 4 points")
    private List<Point> points;

    @OneToMany(mappedBy = "parkingSpot", cascade = CascadeType.REMOVE)
    private List<Reservation> reservations;

    @ManyToOne
    @NotNull(message = "Parking is required")
    private Parking parking;
}
