package com.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BankingException {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class UnauthorizedAccountAccessException extends RuntimeException {
        public UnauthorizedAccountAccessException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidOperationException extends RuntimeException {
        public InvalidOperationException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class AccountNotActiveException extends RuntimeException {
        public AccountNotActiveException(String msg) { super(msg); }
    }
}
