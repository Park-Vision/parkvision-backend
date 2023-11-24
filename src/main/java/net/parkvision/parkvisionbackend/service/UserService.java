package net.parkvision.parkvisionbackend.service;

import lombok.RequiredArgsConstructor;
import net.parkvision.parkvisionbackend.dto.SetPasswordResetDTO;
import net.parkvision.parkvisionbackend.model.Client;
import net.parkvision.parkvisionbackend.model.Role;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.ClientRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${park-vision.password-reset-hour-rule}")
    private int passwordResetHourRule;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Client createClient(String email, String firstName, String lastName, String password) {
        Client client = new Client();
        client.setFirstname(firstName);
        client.setEmail(email);
        client.setLastname(lastName);
        client.setPassword(passwordEncoder.encode(password));
        client.setRole(Role.USER);
        return clientRepository.save(client);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void generatePasswordResetToken(User user, Long timestamp) {
        String token = passwordEncoder.encode(user.getEmail() + timestamp);
        user.setPasswordResetToken(token);
        userRepository.save(user);
    }

    public User getUserByResetToken(String token) {
        return userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void resetPassword(User user, SetPasswordResetDTO setPasswordResetDTO) {
        boolean isValid = passwordEncoder.matches(user.getEmail() + setPasswordResetDTO.getTimestamp(),
                user.getPasswordResetToken());
        if (!isValid) {
            throw new RuntimeException("Invalid token");
        }
        if (System.currentTimeMillis() - setPasswordResetDTO.getTimestamp() > 1000L * 60 * 60 * passwordResetHourRule) {
            throw new RuntimeException("Token expired");
        }
        user.setPassword(passwordEncoder.encode(setPasswordResetDTO.getPassword()));
        user.setPasswordResetToken(null);
        userRepository.save(user);
    }

    @Scheduled(cron = "0 0 * * * *") // every day at midnight
    public void clearPasswordResetToken() {
        getAllUsers().forEach(user -> {
            if (user.getPasswordResetToken() != null) {
                user.setPasswordResetToken(null);
                userRepository.save(user);
            }
        });
    }
}
