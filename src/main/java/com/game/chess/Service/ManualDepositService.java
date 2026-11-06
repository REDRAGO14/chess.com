package com.game.chess.Service;

import com.game.chess.Exception.DepositNotFoundException;
import com.game.chess.Model.DepositStatus;
import com.game.chess.Model.ManualDeposit;
import com.game.chess.Model.User;
import com.game.chess.Repository.ManualDepositRepository;
import com.game.chess.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ManualDepositService {

    private final ManualDepositRepository depositRepository;
    private final SecureUploadService uploadService;
    private final ResilientVerificationService verificationService;
    private final WalletService walletService;
    private final UserRepository userRepository;

    @Transactional
    public ManualDeposit submitDeposit(Long userId, BigDecimal amount, MultipartFile file) {
        // 1) Save the file securely using magic byte inspection and UUID naming
        String storedFilename = uploadService.saveReceipt(file);

        // 2) Generate an internal bank reference since the user didn't supply one
        String generatedRef = "MANUAL-" + UUID.randomUUID();

        // 3) Fetch user reference
        User user = userRepository.getReferenceById(userId);

        // 4) Persist initial deposit record as PENDING
        ManualDeposit deposit = new ManualDeposit();
        deposit.setUser(user);
        deposit.setAmount(amount);
        deposit.setBankReference(generatedRef);
        deposit.setReceiptFile(storedFilename);
        deposit.setStatus(DepositStatus.PENDING);
        deposit = depositRepository.save(deposit);

        // 5) Trigger automated verification asynchronously with fallback protection
        try {
            VerificationResult result = verificationService.verify(generatedRef, amount).join();

            switch (result) {
                case VERIFIED -> {
                    deposit.setStatus(DepositStatus.APPROVED);
                    deposit.setReviewedAt(LocalDateTime.now());
                    depositRepository.save(deposit);

                    // Credit wallet idempotently
                    walletService.credit(userId, amount, "DEPOSIT:" + deposit.getId());
                }
                case REJECTED -> {
                    deposit.setStatus(DepositStatus.REJECTED);
                    deposit.setReviewedAt(LocalDateTime.now());
                    depositRepository.save(deposit);
                }
                case UNAVAILABLE -> {
                    // Bank timed out or circuit is open -> Fallback to admin review queue
                    deposit.setStatus(DepositStatus.PENDING_ADMIN_REVIEW);
                    depositRepository.save(deposit);
                }
            }
        } catch (Exception e) {
            // Safety net: if anything goes wrong in the async pipeline, route to admin review
            deposit.setStatus(DepositStatus.PENDING_ADMIN_REVIEW);
            depositRepository.save(deposit);
        }

        return deposit;
    }

    public ManualDeposit getOrThrow(Long id) {
        return depositRepository.findById(id)
                .orElseThrow(() -> new DepositNotFoundException("Deposit " + id + " not found"));
    }
}