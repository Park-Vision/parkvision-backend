package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Client extends User {
    @OneToMany(mappedBy = "client")
    private List<Car> cars;
}
