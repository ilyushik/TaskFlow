package org.example.authmicroservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.authmicroservice.Configuration.JwtService;
import org.example.authmicroservice.DTO.AuthRequest;
import org.example.authmicroservice.DTO.RegisterRequest;
import org.example.authmicroservice.Kafka.KafkaProducer;
import org.example.authmicroservice.Model.User;
import org.example.authmicroservice.Repository.RoleRepository;
import org.example.authmicroservice.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final KafkaProducer kafkaProducer;

    public String register(RegisterRequest request) {
        if (userRepo.existsUserByUsername(request.getUsername())) {
            throw new RuntimeException("Username taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(roleRepo.findByRole("ROLE_USER"));

        userRepo.save(user);
        return jwtService.generateToken(user);
    }

    public String authenticate(AuthRequest request) {
        User user = userRepo.findUserByUsername(request.getUsername()).orElse(null);

        assert user != null;

        String token = jwtService.generateToken(user);

        kafkaProducer.sendToken(token);
        return token;
    }
}
