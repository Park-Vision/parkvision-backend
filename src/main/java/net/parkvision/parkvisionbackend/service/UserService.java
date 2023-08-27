package net.parkvision.parkvisionbackend.service;

import lombok.RequiredArgsConstructor;
import net.parkvision.parkvisionbackend.model.Role;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String email, String firstName, String lastName, String password) {
        return userRepository.save(
                User.builder()
                        .firstname(firstName)
                        .lastname(lastName)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .role(Role.USER)
                        .build()
        );
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

    }
}
