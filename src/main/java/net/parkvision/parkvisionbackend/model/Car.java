package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Registration number is required")
    private String registrationNumber;

    @NotNull(message = "Color is required")
    private String color;

    @NotNull(message = "Brand is required")
    private String brand;
    
    @ManyToOne
    @NotNull(message = "Client is required")
    private Client client;
}
