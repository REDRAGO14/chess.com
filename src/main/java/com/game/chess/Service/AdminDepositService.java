package com.game.chess.Service;

import com.game.chess.Model.ManualDeposit;
import com.game.chess.Model.DepositStatus;
import com.game.chess.Exception.DepositNotFoundException;
import com.game.chess.Repository.ManualDepositRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDepositService {

    private final ManualDepositRepository depositRepository;
    private final WalletService walletService;
    private final SecureUploadService uploadService;

    public List<ManualDeposit> getDepositsByStatus(DepositStatus status) {
        return depositRepository.findByStatus(status);
    }

    @Transactional
    public ManualDeposit approveDeposit(Long depositId, Long adminId) {
        ManualDeposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new DepositNotFoundException("Deposit " + depositId + " not found"));

        // State machine guard: Only deposits awaiting review can be approved
        if (deposit.getStatus() != DepositStatus.PENDING_ADMIN_REVIEW) {
            throw new IllegalStateException("Deposit cannot be approved. Current status: " + deposit.getStatus());
        }

        deposit.setStatus(DepositStatus.APPROVED);
        deposit.setReviewedBy(adminId);
        deposit.setReviewedAt(LocalDateTime.now());

        // Credit the user's wallet idempotently using a unique transaction reference
        walletService.credit(deposit.getUser().getId(), deposit.getAmount(), "DEPOSIT:" + deposit.getId());

        return depositRepository.save(deposit);
    }

    @Transactional
    public ManualDeposit rejectDeposit(Long depositId, Long adminId, String adminNote) {
        ManualDeposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new DepositNotFoundException("Deposit " + depositId + " not found"));

        if (deposit.getStatus() != DepositStatus.PENDING_ADMIN_REVIEW) {
            throw new IllegalStateException("Deposit cannot be rejected. Current status: " + deposit.getStatus());
        }

        deposit.setStatus(DepositStatus.REJECTED);
        deposit.setReviewedBy(adminId);
        deposit.setReviewedAt(LocalDateTime.now());
        deposit.setAdminNote(adminNote);

        return depositRepository.save(deposit);
    }

    public Resource getReceiptFile(Long depositId) {
        ManualDeposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new DepositNotFoundException("Deposit " + depositId + " not found"));

        // Load file securely via SecureUploadService (prevents path traversal)
        return uploadService.load(deposit.getReceiptFile());
    }
}