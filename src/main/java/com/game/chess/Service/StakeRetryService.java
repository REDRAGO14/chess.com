package com.game.chess.Service;

import com.game.chess.DTO.StakeResponse;
import com.game.chess.Exception.LockConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StakeRetryService {

    private final MatchEscrowService escrowService;

    // IMPORTANT: NOT @Transactional! Each retry must run in a fresh transaction
    // to fetch the latest updated version and balance from the database.
    public StakeResponse stakeWithRetry(Long gameId, Long userId, BigDecimal amount) {
        for (int attempt = 1; ; attempt++) {
            try {
                return escrowService.stakeForMatch(gameId, userId, amount);
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == 3) {
                    throw new LockConflictException("Wallet is busy, please retry");
                }
                // Loop continues automatically, starting a fresh transaction on the next iteration
            }
        }
    }
}