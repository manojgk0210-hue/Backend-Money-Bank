package com.bank.service;

import com.bank.dto.Dto;
import com.bank.exception.BankingException;
import com.bank.model.Account;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository        userRepository;
    private final AccountRepository     accountRepository;
    private final TransactionRepository transactionRepository;

    public Dto.AdminDashboardStats getDashboardStats() {
        long totalUsers        = userRepository.count();
        long totalAccounts     = accountRepository.count();
        long totalTransactions = transactionRepository.count();
        long activeAccounts    = accountRepository.countByStatus(Account.AccountStatus.ACTIVE);
        long suspendedAccounts = accountRepository.countByStatus(Account.AccountStatus.SUSPENDED);

        BigDecimal totalBalance = accountRepository.findAll()
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Dto.AdminDashboardStats.builder()
                .totalUsers(totalUsers)
                .totalAccounts(totalAccounts)
                .totalTransactions(totalTransactions)
                .totalBalanceAcrossBank(totalBalance)
                .activeAccounts(activeAccounts)
                .suspendedAccounts(suspendedAccounts)
                .build();
    }

    public Page<Dto.UserSummary> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toUserSummary);
    }
    public Dto.UserSummary getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BankingException.UserNotFoundException(
                    "User not found with id: " + userId));
        return toUserSummary(user);
    }

    public List<Dto.AccountResponse> getUserAccounts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BankingException.UserNotFoundException(
                    "User not found with id: " + userId));
        return accountRepository.findByUser(user)
                .stream()
                .map(this::toAccountResponse)
                .collect(Collectors.toList());
    }

    public Page<Dto.AccountResponse> getAllAccounts(int page, int size) {
        return accountRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toAccountResponse);
    }

    @Transactional
    public Dto.AccountResponse updateAccountStatus(String accountNumber,
                                                    Dto.UpdateAccountStatusRequest req) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankingException.AccountNotFoundException(
                    "Account not found: " + accountNumber));
        account.setStatus(req.getStatus());
        accountRepository.save(account);
        log.info("Admin updated account {} status to {}", accountNumber, req.getStatus());
        return toAccountResponse(account);
    }

    @Transactional
    public Dto.UserSummary updateUserRole(Long userId, Dto.UpdateUserRoleRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BankingException.UserNotFoundException(
                    "User not found with id: " + userId));
        user.setRole(req.getRole());
        userRepository.save(user);
        log.info("Admin updated user {} role to {}", user.getEmail(), req.getRole());
        return toUserSummary(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BankingException.UserNotFoundException(
                    "User not found with id: " + userId));
        userRepository.delete(user);
        log.info("Admin deleted user: {}", user.getEmail());
    }

    @Transactional
    public Dto.AccountResponse adminFund(Dto.AdminFundRequest req) {
        Account account = accountRepository.findByAccountNumber(req.getAccountNumber())
                .orElseThrow(() -> new BankingException.AccountNotFoundException(
                    "Account not found: " + req.getAccountNumber()));

        boolean isCredit = "CREDIT".equalsIgnoreCase(req.getOperation());

        // Validate sufficient balance for debit
        if (!isCredit && account.getBalance().compareTo(req.getAmount()) < 0) {
            throw new BankingException.InsufficientFundsException(
                "Insufficient balance. Available: ₹" + account.getBalance());
        }

        BigDecimal newBalance = isCredit
            ? account.getBalance().add(req.getAmount())
            : account.getBalance().subtract(req.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);

        // Generate unique reference number
        String ref;
        do {
            ref = "ADM" + java.util.UUID.randomUUID()
                    .toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (transactionRepository.existsByReferenceNumber(ref));

        // Save transaction record so it appears in customer's history too
        com.bank.model.Transaction tx = com.bank.model.Transaction.builder()
                .referenceNumber(ref)
                .type(isCredit
                    ? com.bank.model.Transaction.TransactionType.DEPOSIT
                    : com.bank.model.Transaction.TransactionType.WITHDRAWAL)
                .amount(req.getAmount())
                .balanceAfter(newBalance)
                .description(req.getReason() != null && !req.getReason().isBlank()
                    ? "[ADMIN] " + req.getReason()
                    : "[ADMIN] " + (isCredit ? "Credit adjustment" : "Debit adjustment"))
                .fromAccount(isCredit ? null : account)
                .toAccount(isCredit ? account : null)
                .build();
        transactionRepository.save(tx);

        log.info("Admin {} ₹{} {} account {}. New balance: ₹{}",
            isCredit ? "CREDITED" : "DEBITED",
            req.getAmount(), isCredit ? "to" : "from",
            req.getAccountNumber(), newBalance);

        return toAccountResponse(account);
    }
    public Page<Dto.TransactionResponse> getAllTransactions(int page, int size) {
        return transactionRepository
                .findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toTransactionResponse);
    }

    private Dto.UserSummary toUserSummary(User u) {
        List<Account> accounts = accountRepository.findByUser(u);
        BigDecimal total = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Dto.UserSummary.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .role(u.getRole())
                .createdAt(u.getCreatedAt())
                .totalAccounts(accounts.size())
                .totalBalance(total)
                .build();
    }

    private Dto.AccountResponse toAccountResponse(Account a) {
        return Dto.AccountResponse.builder()
                .id(a.getId())
                .accountNumber(a.getAccountNumber())
                .accountType(a.getAccountType())
                .balance(a.getBalance())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .ownerName(a.getUser().getFullName())
                .ownerEmail(a.getUser().getEmail())
                .build();
    }

    private Dto.TransactionResponse toTransactionResponse(
            com.bank.model.Transaction t) {
        return Dto.TransactionResponse.builder()
                .id(t.getId())
                .referenceNumber(t.getReferenceNumber())
                .type(t.getType())
                .amount(t.getAmount())
                .balanceAfter(t.getBalanceAfter())
                .description(t.getDescription())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .fromAccountNumber(t.getFromAccount() != null
                    ? t.getFromAccount().getAccountNumber() : null)
                .toAccountNumber(t.getToAccount() != null
                    ? t.getToAccount().getAccountNumber() : null)
                .build();
    }
}
