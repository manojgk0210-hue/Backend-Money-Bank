package com.bank.service;

import com.bank.dto.Dto;
import com.bank.exception.BankingException;
import com.bank.model.Account;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Dto.AccountResponse createAccount(User user, Dto.CreateAccountRequest req) {
        String accountNumber = generateUniqueAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(req.getAccountType())
                .user(user)
                .build();

        accountRepository.save(account);
        log.info("Account created: {} [{}] for user: {}", accountNumber, req.getAccountType(), user.getEmail());
        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<Dto.AccountResponse> getMyAccounts(User user) {
        return accountRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Dto.AccountResponse getAccount(String accountNumber, User user) {
        Account account = findByNumber(accountNumber);
        checkOwnership(account, user);
        return toResponse(account);
    }
    public Account findByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankingException.AccountNotFoundException(
                    "Account not found: " + accountNumber));
    }

    public void checkOwnership(Account account, User user) {
        if (!account.getUser().getId().equals(user.getId())) {
            throw new BankingException.UnauthorizedAccountAccessException(
                "You do not have access to account: " + account.getAccountNumber());
        }
    }

    public Dto.AccountResponse toResponse(Account a) {
        return Dto.AccountResponse.builder()
                .id(a.getId())
                .accountNumber(a.getAccountNumber())
                .accountType(a.getAccountType())
                .balance(a.getBalance())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .ownerName(a.getUser().getFullName())
                .build();
    }

    private String generateUniqueAccountNumber() {
        String number;
        do {
            number = "FT" + String.format("%010d", new Random().nextLong(10_000_000_000L));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}
