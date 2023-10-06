package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ParkingModerator extends User {
    @ManyToOne
    private Parking parking; // One-to-one relationship
}
