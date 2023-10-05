package net.parkvision.parkvisionbackend.controller;

import net.parkvision.parkvisionbackend.dto.UserDTO;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService _userService;
    private final ModelMapper _modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        _userService = userService;
        _modelMapper = modelMapper;
    }

    private UserDTO convertToDto(User user) {
        return _modelMapper.map(user, UserDTO.class);
    }

    private User convertToEntity(UserDTO userDTO) {
        return _modelMapper.map(userDTO, User.class);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = _userService.getAllUsers().stream().map(
                this::convertToDto
        ).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = _userService.getUserById(id);
        return ResponseEntity.ok(convertToDto(user));
    }



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
