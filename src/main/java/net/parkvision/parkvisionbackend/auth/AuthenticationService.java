package net.parkvision.parkvisionbackend.auth;

import lombok.RequiredArgsConstructor;
import net.parkvision.parkvisionbackend.config.JwtService;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {

        return buildAuthenticationResponse(
                userService.createUser(
                        registerRequest.getEmail(),
                        registerRequest.getFirstName(),
                        registerRequest.getLastName(),
                        registerRequest.getPassword()
                )
        );
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        return buildAuthenticationResponse(userService.getUserByEmail(authenticationRequest.getEmail()));
    }

    private AuthenticationResponse buildAuthenticationResponse(User user) {
        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }
}
