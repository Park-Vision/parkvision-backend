package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
public class Client extends User {
    private List<Car> cars; // One-to-many relationship
    private List<Reservation> reservations; // One-to-many relationship
}
