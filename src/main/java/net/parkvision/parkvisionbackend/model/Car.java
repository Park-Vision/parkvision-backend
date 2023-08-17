package net.parkvision.parkvisionbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String registrationNumber;
    private String color;
    private Client client;
}
