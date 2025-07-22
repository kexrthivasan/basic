package com.hashedin.huspark.service;

import com.hashedin.huspark.dto.*;
import com.hashedin.huspark.entity.*;
import com.hashedin.huspark.repository.*;
import com.hashedin.huspark.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final BlacklistedTokenRepository blacklistedTokenRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepo.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return AuthResponse.builder().token(token).build();
    }

    public AuthResponse login(AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return AuthResponse.builder().token(token).build();
    }

    public String logout(String token) {
        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Invalid token.");
        }

        if (blacklistedTokenRepo.existsByToken(token)) {
            return "Token already blacklisted.";
        }

        blacklistedTokenRepo.save(BlacklistedToken.builder().token(token).build());
        return "Logout successful.";
    }
}
