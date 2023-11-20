package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.dto.PasswordResetDTO;
import net.parkvision.parkvisionbackend.dto.SetPasswordResetDTO;
import net.parkvision.parkvisionbackend.dto.UserDTO;
import net.parkvision.parkvisionbackend.model.ParkingModerator;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.service.EmailSenderService;
import net.parkvision.parkvisionbackend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService _userService;
    private final ModelMapper modelMapper;
    private final EmailSenderService _emailSenderService;

    @Value("${park-vision.domain-ip}")
    private String domainIp;

    @Autowired
    public UserController(UserService userService,
                          ModelMapper modelMapper,
                          EmailSenderService emailSenderService) {
        _userService = userService;
        this.modelMapper = modelMapper;
        _emailSenderService = emailSenderService;
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        // if user is an instance of ParkingManager then set parkingDTO
        if (user instanceof ParkingModerator) {
            userDTO.setParkingDTO(((ParkingModerator) user).getParking() == null ? null : modelMapper.map(((ParkingModerator) user).getParking(), ParkingDTO.class));
        }

        return userDTO;
    }

    private User convertToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = _userService.getAllUsers().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = _userService.getUserById(id);
        return ResponseEntity.ok(convertToDto(user));
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        User updatedUser = _userService.updateUser(user);
        return ResponseEntity.ok(convertToDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        _userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(
            @RequestBody PasswordResetDTO passwordResetDTO
    ) {
        try {
            User user = _userService.getUserByEmail(passwordResetDTO.getEmail());
            Long timestamp = System.currentTimeMillis();
            _userService.generatePasswordResetToken(user, timestamp);
            _emailSenderService.sendHtmlEmailPasswordReset(
                    user.getFirstname(),
                    user.getLastname(),
                    user.getEmail(),
                    "ParkVision password reset",
                    "Here is the link to reset your password: " + domainIp + "/reset-password?token="
                            + user.getPasswordResetToken() + "&timestamp=" + timestamp
                    );
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/setPasswordFromReset")
    public ResponseEntity<Void> resetPassword(
            @RequestBody SetPasswordResetDTO setPasswordResetDTO
    ) {
        try {
            User user = _userService.getUserByResetToken(setPasswordResetDTO.getToken());

            if(user == null) {
                return ResponseEntity.ok().build();
            }
            _userService.resetPassword(user, setPasswordResetDTO);
        }
        catch (Exception e) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok().build();
    }


}
