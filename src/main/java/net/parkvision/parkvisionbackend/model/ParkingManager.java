package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ParkingManager extends User {
    @ManyToOne
    private Parking parking;
}
