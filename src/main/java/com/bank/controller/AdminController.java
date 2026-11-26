package com.bank.controller;

import com.bank.dto.Dto;
import com.bank.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    @GetMapping("/stats")
    public ResponseEntity<Dto.ApiResponse<Dto.AdminDashboardStats>> getStats() {
        return ResponseEntity.ok(Dto.ApiResponse.ok("Stats retrieved",
                adminService.getDashboardStats()));
    }
    @GetMapping("/users")
    public ResponseEntity<Dto.ApiResponse<Page<Dto.UserSummary>>> getAllUsers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(Dto.ApiResponse.ok("Users retrieved",
                adminService.getAllUsers(page, size)));
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<Dto.ApiResponse<Dto.UserSummary>> getUser(
            @PathVariable Long id) {
        return ResponseEntity.ok(Dto.ApiResponse.ok("User retrieved",
                adminService.getUserById(id)));
    }
    @GetMapping("/users/{id}/accounts")
    public ResponseEntity<Dto.ApiResponse<List<Dto.AccountResponse>>> getUserAccounts(
            @PathVariable Long id) {
        return ResponseEntity.ok(Dto.ApiResponse.ok("User accounts retrieved",
                adminService.getUserAccounts(id)));
    }
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<Dto.ApiResponse<Dto.UserSummary>> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody Dto.UpdateUserRoleRequest request) {
        return ResponseEntity.ok(Dto.ApiResponse.ok("User role updated",
                adminService.updateUserRole(id, request)));
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Dto.ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Dto.ApiResponse.ok("User deleted"));
    }
    @GetMapping("/accounts")
    public ResponseEntity<Dto.ApiResponse<Page<Dto.AccountResponse>>> getAllAccounts(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(Dto.ApiResponse.ok("Accounts retrieved",
                adminService.getAllAccounts(page, size)));
    }
    @PatchMapping("/accounts/{accountNumber}/status")
    public ResponseEntity<Dto.ApiResponse<Dto.AccountResponse>> updateAccountStatus(
            @PathVariable String accountNumber,
            @Valid @RequestBody Dto.UpdateAccountStatusRequest request) {
        return ResponseEntity.ok(Dto.ApiResponse.ok("Account status updated",
                adminService.updateAccountStatus(accountNumber, request)));
    }
    @PostMapping("/accounts/fund")
    public ResponseEntity<Dto.ApiResponse<Dto.AccountResponse>> fundAccount(
            @Valid @RequestBody Dto.AdminFundRequest request) {
        Dto.AccountResponse data = adminService.adminFund(request);
        String msg = "CREDIT".equalsIgnoreCase(request.getOperation())
            ? "₹" + request.getAmount() + " credited successfully"
            : "₹" + request.getAmount() + " debited successfully";
        return ResponseEntity.ok(Dto.ApiResponse.ok(msg, data));
    }

    @GetMapping("/transactions")
    public ResponseEntity<Dto.ApiResponse<Page<Dto.TransactionResponse>>> getAllTransactions(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(Dto.ApiResponse.ok("Transactions retrieved",
                adminService.getAllTransactions(page, size)));
    }
}
