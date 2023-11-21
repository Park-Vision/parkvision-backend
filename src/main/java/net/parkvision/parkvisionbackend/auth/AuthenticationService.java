package net.parkvision.parkvisionbackend.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import net.parkvision.parkvisionbackend.config.JwtService;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthenticationResponse register(RegisterRequest registerRequest) {

        return buildAuthenticationResponse(
                userService.createClient(
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
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("userId", user.getId() + "");
        claims.put("role", user.getRole());
        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(claims, user))
                .build();
    }

    public AuthenticationResponse refresh(String token) {
        User user = userService.getUserByEmail(jwtService.extractUsername(token));
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(user.getEmail());
        if (jwtService.isTokenValidRefresh(token, userDetails)){
            return buildAuthenticationResponse(user);
        }
        System.out.println("can't refresh");
        return null;
    }
}
