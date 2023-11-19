package net.parkvision.parkvisionbackend.dto;

import lombok.Data;

@Data
public class SetPasswordResetDTO {
    private String token;
    private String password;
}
