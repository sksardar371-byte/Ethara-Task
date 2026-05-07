package com.teamtask.controller;

import com.teamtask.dto.AuthDtos.AuthResponse;
import com.teamtask.dto.AuthDtos.LoginRequest;
import com.teamtask.dto.AuthDtos.SignupRequest;
import com.teamtask.model.Role;
import com.teamtask.model.User;
import com.teamtask.repository.UserRepository;
import com.teamtask.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwtService) {
        this.users = users;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }
        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(encoder.encode(request.password()));
        user.setRole(users.count() == 0 ? Role.ADMIN : Role.MEMBER);
        users.save(user);
        return response(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return response(user);
    }

    private AuthResponse response(User user) {
        String token = jwtService.createToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
