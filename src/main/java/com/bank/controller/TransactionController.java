package com.bank.controller;

import com.bank.dto.Dto;
import com.bank.model.User;
import com.bank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    @PostMapping("/deposit")
    public ResponseEntity<Dto.ApiResponse<Dto.TransactionResponse>> deposit(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody Dto.DepositRequest request) {

        Dto.TransactionResponse data = transactionService.deposit(request, user);
        return ResponseEntity.ok(Dto.ApiResponse.ok("Deposit successful", data));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Dto.ApiResponse<Dto.TransactionResponse>> withdraw(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody Dto.WithdrawRequest request) {

        Dto.TransactionResponse data = transactionService.withdraw(request, user);
        return ResponseEntity.ok(Dto.ApiResponse.ok("Withdrawal successful", data));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Dto.ApiResponse<Dto.TransactionResponse>> transfer(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody Dto.TransferRequest request) {

        Dto.TransactionResponse data = transactionService.transfer(request, user);
        return ResponseEntity.ok(Dto.ApiResponse.ok("Transfer successful", data));
    }
    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<Dto.ApiResponse<Page<Dto.TransactionResponse>>> history(
            @AuthenticationPrincipal User user,
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Dto.TransactionResponse> data =
                transactionService.getHistory(accountNumber, user, page, size);
        return ResponseEntity.ok(Dto.ApiResponse.ok("Transaction history retrieved", data));
    }
}
