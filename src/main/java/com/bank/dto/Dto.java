package com.bank.dto;

import com.bank.model.Account;
import com.bank.model.Role;
import com.bank.model.Transaction;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Dto {
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "First name is required")
        private String firstName;
        @NotBlank(message = "Last name is required")
        private String lastName;
        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;
        @NotBlank(message = "Phone number is required")
        private String phone;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AdminRegisterRequest {
        @NotBlank(message = "First name is required")
        private String firstName;
        @NotBlank(message = "Last name is required")
        private String lastName;
        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;
        @NotBlank(message = "Phone number is required")
        private String phone;
        // Secret key to prevent public admin registration
        @NotBlank(message = "Admin secret key is required")
        private String adminSecretKey;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;
        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String tokenType;
        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
        private Role role;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateAccountRequest {
        @NotNull(message = "Account type is required")
        private Account.AccountType accountType;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AccountResponse {
        private Long id;
        private String accountNumber;
        private Account.AccountType accountType;
        private BigDecimal balance;
        private Account.AccountStatus status;
        private LocalDateTime createdAt;
        private String ownerName;
        private String ownerEmail;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DepositRequest {
        @NotBlank(message = "Account number is required")
        private String accountNumber;
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Minimum deposit is 1.00")
        private BigDecimal amount;
        private String description;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class WithdrawRequest {
        @NotBlank(message = "Account number is required")
        private String accountNumber;
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Minimum withdrawal is 1.00")
        private BigDecimal amount;
        private String description;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TransferRequest {
        @NotBlank(message = "Source account number is required")
        private String fromAccountNumber;
        @NotBlank(message = "Destination account number is required")
        private String toAccountNumber;
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Minimum transfer is 1.00")
        private BigDecimal amount;
        private String description;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TransactionResponse {
        private Long id;
        private String referenceNumber;
        private Transaction.TransactionType type;
        private BigDecimal amount;
        private BigDecimal balanceAfter;
        private String description;
        private Transaction.TransactionStatus status;
        private LocalDateTime createdAt;
        private String fromAccountNumber;
        private String toAccountNumber;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private Role role;
        private LocalDateTime createdAt;
        private int totalAccounts;
        private BigDecimal totalBalance;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AdminDashboardStats {
        private long totalUsers;
        private long totalAccounts;
        private long totalTransactions;
        private BigDecimal totalBalanceAcrossBank;
        private long activeAccounts;
        private long suspendedAccounts;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateAccountStatusRequest {
        @NotNull(message = "Status is required")
        private Account.AccountStatus status;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateUserRoleRequest {
        @NotNull(message = "Role is required")
        private Role role;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AdminFundRequest {
        @NotBlank(message = "Account number is required")
        private String accountNumber;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Minimum amount is 1.00")
        private BigDecimal amount;

        @NotBlank(message = "Operation is required")
        private String operation;

        private String reason;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public static <T> ApiResponse<T> ok(String message, T data) {
            return ApiResponse.<T>builder().success(true).message(message).data(data).build();
        }
        public static <T> ApiResponse<T> ok(String message) {
            return ApiResponse.<T>builder().success(true).message(message).build();
        }
        public static <T> ApiResponse<T> fail(String message) {
            return ApiResponse.<T>builder().success(false).message(message).build();
        }
    }
}
