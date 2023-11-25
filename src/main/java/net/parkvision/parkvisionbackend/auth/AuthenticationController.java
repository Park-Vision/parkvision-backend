package net.parkvision.parkvisionbackend.auth;

import lombok.RequiredArgsConstructor;
import net.parkvision.parkvisionbackend.dto.RefreshTokenDTO;
import net.parkvision.parkvisionbackend.model.User;
import net.parkvision.parkvisionbackend.service.EmailSenderService;
import net.parkvision.parkvisionbackend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final EmailSenderService emailSenderService;
    private final UserService userService;
    @Value("${park-vision.domain-ip}")
    private String domainIp;

    @Value("${park-vision.password-reset-hour-rule}")
    private int passwordResetHourRule;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest registerRequest
    ) {
        AuthenticationResponse register = authenticationService.register(registerRequest);
        try {
            emailSenderService.sendHtmlEmailRegistrationCreated(
                    registerRequest.getFirstName(),
                    registerRequest.getLastName(),
                    registerRequest.getEmail(),
                    "ParkVision registration confirmation");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(register);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registerManager")
    public ResponseEntity<Void> registerManager(
            @RequestBody RegisterRequest registerRequest
    ) {
        authenticationService.registerManager(registerRequest);
        try {
            User user = userService.getUserByEmail(registerRequest.getEmail());
            Long timestamp = System.currentTimeMillis();
            if (user != null){
                userService.generatePasswordResetToken(user, timestamp);
                emailSenderService.sendHtmlEmailPasswordReset(
                        user.getFirstname(),
                        user.getLastname(),
                        user.getEmail(),
                        "Manager registration, reset your password",
                        "ParkVision manager registration",
                        "Reset your password using link in the message. " +
                                "After setting your new password you will be able to log in our system."
                                + "This link will expire in " + passwordResetHourRule + " hour.",
                        domainIp + "/reset-password?token="
                                + user.getPasswordResetToken() + "&timestamp=" + timestamp
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(
            @RequestBody RefreshTokenDTO refreshTokenDTO
    ) {
        AuthenticationResponse authenticationResponse = authenticationService.refreshToken(refreshTokenDTO.getToken());
        if (authenticationResponse != null) {
            return ResponseEntity.ok(authenticationResponse);
        }
        return new ResponseEntity<>("Too late for refresh", HttpStatus.UNAUTHORIZED);
    }
}
