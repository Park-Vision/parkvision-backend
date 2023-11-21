package net.parkvision.parkvisionbackend.service;

import lombok.RequiredArgsConstructor;
import net.parkvision.parkvisionbackend.model.Client;
import net.parkvision.parkvisionbackend.model.Role;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.repository.ClientRepository;
import net.parkvision.parkvisionbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Client createClient(String email, String firstName, String lastName, String password) {
        Client client = new Client();
        client.setFirstname(firstName);
        client.setEmail(email);
        client.setLastname(lastName);
        client.setPassword(passwordEncoder.encode(password));
        return clientRepository.save(client);
    }

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
}
