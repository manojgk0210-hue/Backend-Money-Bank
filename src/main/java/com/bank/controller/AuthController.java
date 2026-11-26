package com.bank.controller;

import com.bank.dto.Dto;
import com.bank.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<Dto.ApiResponse<Dto.AuthResponse>> register(
            @Valid @RequestBody Dto.RegisterRequest request) {
        Dto.AuthResponse data = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Dto.ApiResponse.ok("Registration successful", data));
    }
    @PostMapping("/register/admin")
    public ResponseEntity<Dto.ApiResponse<Dto.AuthResponse>> registerAdmin(
            @Valid @RequestBody Dto.AdminRegisterRequest request) {
        Dto.AuthResponse data = authService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Dto.ApiResponse.ok("Admin registration successful", data));
    }

    @PostMapping("/login")
    public ResponseEntity<Dto.ApiResponse<Dto.AuthResponse>> login(
            @Valid @RequestBody Dto.LoginRequest request) {
        Dto.AuthResponse data = authService.login(request);
        return ResponseEntity.ok(Dto.ApiResponse.ok("Login successful", data));
    }
}
