package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
public class Drone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Model is required")
    private String model;

    @NotNull(message = "Serial number is required")
    private String serialNumber;

    @NotNull(message = "Drone key is required")
    private String droneKey;
    @ManyToOne
    @NotNull(message = "Parking is required")
    private Parking parking;
    @OneToMany(mappedBy = "drone")
    private List<DroneMission> droneMission;
}
