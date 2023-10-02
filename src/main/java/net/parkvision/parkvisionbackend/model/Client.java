package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Client extends User {
    @OneToMany
    private List<Car> cars; // One-to-many relationship
    @OneToMany
    private List<Reservation> reservations; // One-to-many relationship
}
