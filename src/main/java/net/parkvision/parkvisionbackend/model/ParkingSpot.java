package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
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
    private String spotNumber;
    private boolean active;

    @Size(min = 4, max = 4, message = "ParkingSpot must have 4 points")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parkingSpot")
    private List<Point> points;

    @OneToMany(mappedBy = "parkingSpot", cascade = CascadeType.REMOVE)
    private List<Reservation> reservations;
    @ManyToOne
    private Parking parking; // Many-to-one relationship


    public void setPoints(List<Point> points) {
        if(points.size() != 4)
            throw new IllegalArgumentException("ParkingSpot must have 4 points");
        this.points = points;
    }
}
