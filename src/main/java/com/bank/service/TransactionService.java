package com.bank.service;

import com.bank.dto.Dto;
import com.bank.exception.BankingException;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository     accountRepository;
    private final AccountService        accountService;

    @Transactional
    public Dto.TransactionResponse deposit(Dto.DepositRequest req, User user) {
        Account account = accountService.findByNumber(req.getAccountNumber());
        accountService.checkOwnership(account, user);
        assertActive(account);

        account.setBalance(account.getBalance().add(req.getAmount()));
        accountRepository.save(account);

        Transaction tx = save(null, account, req.getAmount(), account.getBalance(),
                Transaction.TransactionType.DEPOSIT, req.getDescription());

        log.info("Deposit ₹{} → account {}", req.getAmount(), account.getAccountNumber());
        return toResponse(tx);
    }

    @Transactional
    public Dto.TransactionResponse withdraw(Dto.WithdrawRequest req, User user) {
        Account account = accountService.findByNumber(req.getAccountNumber());
        accountService.checkOwnership(account, user);
        assertActive(account);
        assertSufficientBalance(account, req.getAmount());

        account.setBalance(account.getBalance().subtract(req.getAmount()));
        accountRepository.save(account);

        Transaction tx = save(account, null, req.getAmount(), account.getBalance(),
                Transaction.TransactionType.WITHDRAWAL, req.getDescription());

        log.info("Withdrawal ₹{} ← account {}", req.getAmount(), account.getAccountNumber());
        return toResponse(tx);
    }

    @Transactional
    public Dto.TransactionResponse transfer(Dto.TransferRequest req, User user) {
        if (req.getFromAccountNumber().equals(req.getToAccountNumber())) {
            throw new BankingException.InvalidOperationException(
                "Source and destination accounts cannot be the same");
        }

        Account from = accountService.findByNumber(req.getFromAccountNumber());
        Account to   = accountService.findByNumber(req.getToAccountNumber());

        accountService.checkOwnership(from, user);
        assertActive(from);
        assertActive(to);
        assertSufficientBalance(from, req.getAmount());

        from.setBalance(from.getBalance().subtract(req.getAmount()));
        to.setBalance(to.getBalance().add(req.getAmount()));
        accountRepository.save(from);
        accountRepository.save(to);

        Transaction tx = save(from, to, req.getAmount(), from.getBalance(),
                Transaction.TransactionType.TRANSFER, req.getDescription());

        log.info("Transfer ₹{}: {} → {}", req.getAmount(),
                from.getAccountNumber(), to.getAccountNumber());
        return toResponse(tx);
    }

    @Transactional(readOnly = true)
    public Page<Dto.TransactionResponse> getHistory(String accountNumber, User user, int page, int size) {
        Account account = accountService.findByNumber(accountNumber);
        accountService.checkOwnership(account, user);

        return transactionRepository
                .findAllByAccount(account, PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toResponse);
    }

    private void assertActive(Account account) {
        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new BankingException.AccountNotActiveException(
                "Account " + account.getAccountNumber() + " is not active (status: " + account.getStatus() + ")");
        }
    }

    private void assertSufficientBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BankingException.InsufficientFundsException(
                "Insufficient funds. Available balance: ₹" + account.getBalance());
        }
    }

    private Transaction save(Account from, Account to, BigDecimal amount,
                              BigDecimal balanceAfter, Transaction.TransactionType type,
                              String description) {
        String ref;
        do {
            ref = "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (transactionRepository.existsByReferenceNumber(ref));

        return transactionRepository.save(
            Transaction.builder()
                .referenceNumber(ref)
                .type(type)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .description(description)
                .fromAccount(from)
                .toAccount(to)
                .build()
        );
    }

    private Dto.TransactionResponse toResponse(Transaction t) {
        return Dto.TransactionResponse.builder()
                .id(t.getId())
                .referenceNumber(t.getReferenceNumber())
                .type(t.getType())
                .amount(t.getAmount())
                .balanceAfter(t.getBalanceAfter())
                .description(t.getDescription())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .fromAccountNumber(t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null)
                .toAccountNumber(t.getToAccount() != null ? t.getToAccount().getAccountNumber() : null)
                .build();
    }
}
