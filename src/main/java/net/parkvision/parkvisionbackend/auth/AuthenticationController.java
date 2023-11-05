package net.parkvision.parkvisionbackend.auth;

import lombok.RequiredArgsConstructor;
import net.parkvision.parkvisionbackend.service.EmailSenderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        AuthenticationResponse register = authenticationService.register(request);
        try {
            emailSenderService.sendHtmlEmailRegistrationCreated(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    "ParkVision registration confirmation");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(register);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(
            @RequestBody String token
    ) {
        AuthenticationResponse authenticationResponse = authenticationService.refresh(token);
        if (authenticationResponse != null) {
            return ResponseEntity.ok(authenticationResponse);
        }
        return new ResponseEntity<>("Too late", HttpStatus.UNAUTHORIZED);
    }
}
