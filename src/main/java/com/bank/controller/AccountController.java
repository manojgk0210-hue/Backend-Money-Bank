package com.bank.controller;

import com.bank.dto.Dto;
import com.bank.model.User;
import com.bank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    @PostMapping
    public ResponseEntity<Dto.ApiResponse<Dto.AccountResponse>> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody Dto.CreateAccountRequest request) {

        Dto.AccountResponse data = accountService.createAccount(user, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Dto.ApiResponse.ok("Account opened successfully", data));
    }
    @GetMapping
    public ResponseEntity<Dto.ApiResponse<List<Dto.AccountResponse>>> getAll(
            @AuthenticationPrincipal User user) {

        List<Dto.AccountResponse> data = accountService.getMyAccounts(user);
        return ResponseEntity.ok(Dto.ApiResponse.ok("Accounts retrieved", data));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Dto.ApiResponse<Dto.AccountResponse>> getOne(
            @AuthenticationPrincipal User user,
            @PathVariable String accountNumber) {

        Dto.AccountResponse data = accountService.getAccount(accountNumber, user);
        return ResponseEntity.ok(Dto.ApiResponse.ok("Account retrieved", data));
    }
}
