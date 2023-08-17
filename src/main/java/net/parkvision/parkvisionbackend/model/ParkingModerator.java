package net.parkvision.parkvisionbackend.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParkingModerator extends User{
    private List<Parking> parkings; // One-to-many relationship
}
