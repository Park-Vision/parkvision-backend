package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

@Data
public class CarDTO {
    private Long id;
    private String registrationNumber;
    private String color;
    private String brand;
    private UserDTO userDTO;
}