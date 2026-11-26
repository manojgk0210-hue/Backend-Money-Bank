package com.bank.service;

import com.bank.dto.Dto;
import com.bank.exception.BankingException;
import com.bank.model.Role;
import com.bank.model.User;
import com.bank.repository.UserRepository;
import com.bank.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtService            jwtService;
    private final AuthenticationManager authManager;

    // ye wahi key jiska power bs mere pas hai
    @Value("${app.admin.secret-key:FINTECH_ADMIN_2024}")
    private String adminSecretKey;

    @Transactional
    public Dto.AuthResponse register(Dto.RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BankingException.EmailAlreadyExistsException(
                "Email already registered: " + req.getEmail());
        }
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user);
        log.info("New customer registered: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    @Transactional
    public Dto.AuthResponse registerAdmin(Dto.AdminRegisterRequest req) {
        // Validate secret key first
        if (!adminSecretKey.equals(req.getAdminSecretKey())) {
            throw new BankingException.InvalidOperationException(
                "Invalid admin secret key. Access denied.");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BankingException.EmailAlreadyExistsException(
                "Email already registered: " + req.getEmail());
        }
        User admin = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
        log.info("New ADMIN registered: {}", admin.getEmail());
        return buildAuthResponse(admin);
    }

    public Dto.AuthResponse login(Dto.LoginRequest req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                req.getEmail().toLowerCase().trim(),
                req.getPassword()
            )
        );
        User user = userRepository.findByEmail(req.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BankingException.UserNotFoundException("User not found"));
        log.info("User logged in: {} [{}]", user.getEmail(), user.getRole());
        return buildAuthResponse(user);
    }

    private Dto.AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        return Dto.AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
