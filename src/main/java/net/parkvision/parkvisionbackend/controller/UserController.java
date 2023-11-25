package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.*;
import net.parkvision.parkvisionbackend.model.Parking;
import net.parkvision.parkvisionbackend.model.ParkingManager;
import net.parkvision.parkvisionbackend.model.Role;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.service.EmailSenderService;
import net.parkvision.parkvisionbackend.service.ParkingService;
import net.parkvision.parkvisionbackend.service.RequestContext;
import net.parkvision.parkvisionbackend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ParkingService parkingService;
    private final ModelMapper modelMapper;
    private final EmailSenderService emailSenderService;

    @Value("${park-vision.domain-ip}")
    private String domainIp;

    @Value("${park-vision.password-reset-hour-rule}")
    private int passwordResetHourRule;

    @Autowired
    public UserController(UserService userService,
                          ParkingService parkingService, ModelMapper modelMapper,
                          EmailSenderService emailSenderService) {
        this.userService = userService;
        this.parkingService = parkingService;
        this.modelMapper = modelMapper;
        this.emailSenderService = emailSenderService;
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        if (user instanceof ParkingManager) {
            userDTO.setParkingDTO(((ParkingManager) user).getParking() == null ? null :
                    modelMapper.map(((ParkingManager) user).getParking(), ParkingDTO.class));
        }

        return userDTO;
    }

    private User convertToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/managers")
    public ResponseEntity<List<UserDTO>> getAllManagers() {
        List<UserDTO> managers = userService.getAllManagers().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
        return ResponseEntity.ok(managers);
    }

    @PreAuthorize("hasAnyRole('USER', 'PARKING_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(convertToDto(user));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        User client = RequestContext.getUserFromRequest();
        if(client == null){
            return ResponseEntity.badRequest().build();
        }
        Optional<User> user = userService.getCurrentUserById(userDTO.getId());
        if(!user.isPresent()){
            return ResponseEntity.notFound().build();
        } else {
            if(!user.get().getId().equals(client.getId())){
                return ResponseEntity.badRequest().build();
            }
        }
        try {
            User userUpdated = userService.updateUser(convertToEntity(userDTO));
            return ResponseEntity.ok(convertToDto(userUpdated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(
            @RequestBody PasswordResetDTO passwordResetDTO
    ) {
        try {
            User user = userService.getUserByEmail(passwordResetDTO.getEmail());
            Long timestamp = System.currentTimeMillis();
            userService.generatePasswordResetToken(user, timestamp);
            emailSenderService.sendHtmlEmailPasswordReset(
                    user.getFirstname(),
                    user.getLastname(),
                    user.getEmail(),
                    "Password reset link",
                    "ParkVision password reset",
                    "Here is the link to reset your password. "
                            + "This link will expire in " + passwordResetHourRule + " hour.",
                    domainIp + "/reset-password?token="
                            + user.getPasswordResetToken() + "&timestamp=" + timestamp
            );
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.accepted().build();
        }
    }

    @PostMapping("/setPasswordFromReset")
    public ResponseEntity<Void> resetPassword(
            @RequestBody SetPasswordResetDTO setPasswordResetDTO
    ) {
        try {
            User user = userService.getUserByResetToken(setPasswordResetDTO.getToken());

            if (user == null) {
                return ResponseEntity.ok().build();
            }
            userService.resetPassword(user, setPasswordResetDTO);
        } catch (Exception e) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/updatePassword")
    public ResponseEntity<UserDTO> updatePassword(
            @RequestBody SetNewPasswordDTO setNewPasswordDTO
            ) {
        User user = userService.getUserById(setNewPasswordDTO.getId());
        if(user == null) {
            return ResponseEntity.ok().build();
        }
        userService.updatePassword(user, setNewPasswordDTO);
        return ResponseEntity.ok(convertToDto(user));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/updateName")
    public ResponseEntity<UserDTO> updateName(
            @RequestBody SetNewNameDTO setNewNameDTO
    ) {
        User user = userService.getUserById(setNewNameDTO.getId());
        if(user == null) {
            return ResponseEntity.ok().build();
        }
        user.setFirstname(setNewNameDTO.getFirstName());
        user.setLastname(setNewNameDTO.getLastName());
        userService.updateUser(user);
        return ResponseEntity.ok(convertToDto(user));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/disableUser/{id}")
    public ResponseEntity<Void> disableUser(
            @PathVariable Long id
    ) {
        User user = userService.getUserById(id);
        if(user == null) {
            return ResponseEntity.ok().build();
        }
        user.setEmail(null);
        userService.updateUser(user);
        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/assignParking")
    public ResponseEntity<UserDTO> updateName(
            @RequestBody AssignParkingDTO assignParkingDTO
    ) {
        User user = userService.getUserById(assignParkingDTO.getUserId());
        if(user == null) {
            return ResponseEntity.ok().build();
        }
        if (!user.getRole().equals(Role.PARKING_MANAGER)){
            return ResponseEntity.ok().build();
        }
        ParkingManager parkingManager = (ParkingManager) user;
        Optional<Parking> parking = parkingService.getParkingById(assignParkingDTO.getParkingId());
        parking.ifPresent(parkingManager::setParking);
        userService.updateUser(parkingManager);
        return ResponseEntity.ok(convertToDto(user));
    }
}
