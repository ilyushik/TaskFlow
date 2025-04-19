package org.example.authmicroservice.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.authmicroservice.DTO.AuthRequest;
import org.example.authmicroservice.DTO.AuthResponse;
import org.example.authmicroservice.DTO.RegisterRequest;
import org.example.authmicroservice.Model.User;
import org.example.authmicroservice.Repository.UserRepository;
import org.example.authmicroservice.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Operations related to authentication")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Registration")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("username", "Username cannot be empty"));
        }
        if (request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("password", "Password cannot be empty"));
        }
        if (request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("email", "Email cannot be empty"));
        }
        if (userRepository.existsUserByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("username", "Username already exists"));
        }
        if (userRepository.existsUserByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("email", "Email already exists"));
        }

        String token = authService.register(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Operation(summary = "Login")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        User user = userRepository.findUserByUsername(request.getUsername()).orElse(null);
        if (request.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("username", "Username cannot be empty"));
        }
        if (request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("password", "Password cannot be empty"));
        }
        if (!userRepository.existsUserByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("username", "Username is not exist"));
        }
        assert user != null;
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("password", "Password does not match"));
        }

        String token = authService.authenticate(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
