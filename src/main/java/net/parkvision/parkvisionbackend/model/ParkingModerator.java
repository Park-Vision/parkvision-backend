package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParkingModerator extends User{
    @OneToOne
    private Parking parking; // One-to-one relationship
}
