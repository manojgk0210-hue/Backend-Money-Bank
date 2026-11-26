package com.bank.exception;

import com.bank.dto.Dto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BankingException.UserNotFoundException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleUserNotFound(BankingException.UserNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BankingException.EmailAlreadyExistsException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleEmailExists(BankingException.EmailAlreadyExistsException ex) {
        return response(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(BankingException.AccountNotFoundException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleAccountNotFound(BankingException.AccountNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BankingException.InsufficientFundsException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleInsufficientFunds(BankingException.InsufficientFundsException ex) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BankingException.UnauthorizedAccountAccessException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleUnauthorized(BankingException.UnauthorizedAccountAccessException ex) {
        return response(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(BankingException.InvalidOperationException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleInvalidOp(BankingException.InvalidOperationException ex) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BankingException.AccountNotActiveException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleAccountNotActive(BankingException.AccountNotActiveException ex) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return response(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleDisabled(DisabledException ex) {
        return response(HttpStatus.UNAUTHORIZED, "Account is disabled");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Dto.ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Dto.ApiResponse.<Map<String, String>>builder()
                        .success(false).message("Validation failed").data(errors).build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Dto.ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unhandled error: ", ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<Dto.ApiResponse<Void>> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Dto.ApiResponse.fail(message));
    }
}
