package net.parkvision.parkvisionbackend.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import net.parkvision.parkvisionbackend.model.Role;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Role role;
    private ParkingDTO parkingDTO;
}
