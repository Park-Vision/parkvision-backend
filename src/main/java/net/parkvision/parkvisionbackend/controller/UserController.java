package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.ParkingDTO;
import net.parkvision.parkvisionbackend.dto.UserDTO;
import net.parkvision.parkvisionbackend.model.ParkingModerator;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        _userService = userService;
        this.modelMapper = modelMapper;
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
}
