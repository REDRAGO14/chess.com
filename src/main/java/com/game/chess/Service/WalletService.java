package com.game.chess.Service;

import com.game.chess.Exception.InsufficientBalanceException;
import com.game.chess.Exception.InvalidAmountException;
import com.game.chess.Exception.UserNotFound;
import com.game.chess.Exception.WalletNotFoundException;
import com.game.chess.Model.TransactionType;
import com.game.chess.Model.User;
import com.game.chess.Model.Wallet;
import com.game.chess.Model.WalletAuditLog;
import com.game.chess.Repository.UserRepository;
import com.game.chess.Repository.WalletAuditLogRepository;
import com.game.chess.Repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletAuditLogRepository auditRepository;
    private final UserRepository userRepository;

    @Transactional
    public Wallet getOrCreateWallet(Long userId) {

        return walletRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFound("User " + userId + " not found"));
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            return walletRepository.save(wallet);
        });
    }


    @Transactional
    public Wallet credit(Long userId, BigDecimal amount, String reference) {
        checkAmount(amount);
        Wallet wallet = getOrCreateWallet(userId);

        if (auditRepository.existsByTypeAndReference(TransactionType.CREDIT, reference)) {

            return wallet;
        }
        return applyChange(wallet, amount, TransactionType.CREDIT, reference);
    }

    @Transactional
    public Wallet debit(Long userId, BigDecimal amount, TransactionType type, String reference) {
        checkAmount(amount);
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("No wallet for user " + userId));


        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Balance " + wallet.getBalance() + " is lower than " + amount);
        }

        return applyChange(wallet, amount.negate(), type, reference);
    }

    private Wallet applyChange(Wallet wallet, BigDecimal delta, TransactionType type, String reference) {
        wallet.setBalance(wallet.getBalance().add(delta));
        Wallet saved = walletRepository.save(wallet);

        WalletAuditLog log = new WalletAuditLog();
        log.setWallet(saved);
        log.setType(type);
        log.setAmount(delta.abs());
        log.setBalanceAfter(saved.getBalance());
        log.setReference(reference);
        auditRepository.save(log);
        return saved;
    }

    private void checkAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2) {
            throw new InvalidAmountException("Amount must be positive with at most 2 decimals");
        }

    }

}