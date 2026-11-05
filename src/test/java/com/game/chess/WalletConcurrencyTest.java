package com.game.chess;

import com.game.chess.Model.*;
import com.game.chess.Repository.*;
import com.game.chess.Service.StakeRetryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class WalletConcurrencyTest {

    @Autowired
    private StakeRetryService stakeRetryService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MatchEscrowRepository escrowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void twoParallelStakesOnOneWalletOnlyOneWins() throws Exception {
        // 1. Create test user
        User user = new User();
        user.setUserName("dagim_test");
        user.setEmail("dagim@chess.com");
        user.setPassword("password123");
        user = userRepository.save(user);

        // 2. Set up wallet with exactly 100.00
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("100.00"));
        walletRepository.save(wallet);

        // 3. Create two pending games where this user is the white player
        Game game1 = new Game();
        game1.setWhiteplayer(user);
        game1.setStatus(GameStatus.WAITING);
        game1 = gameRepository.save(game1);

        Game game2 = new Game();
        game2.setWhiteplayer(user);
        game2.setStatus(GameStatus.WAITING);
        game2 = gameRepository.save(game2);

        // 4. Set up two parallel threads attempting to stake 80.00 at the exact same time
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        Long userId = user.getId();
        Long game1Id = game1.getId();
        Long game2Id = game2.getId();

        for (Long gameId : List.of(game1Id, game2Id)) {
            executor.submit(() -> {
                try {
                    latch.await(); // Hold execution until signal
                    stakeRetryService.stakeWithRetry(gameId, userId, new BigDecimal("80.00"));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
        }

        // Release both threads simultaneously
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // 5. Assertions
        // Exactly ONE stake should succeed; the second attempt should fail (insufficient balance / collision)
        assertEquals(1, successCount.get(), "Only one staking attempt should succeed");
        assertEquals(1, failureCount.get(), "The second attempt should fail");

        // Wallet balance must be 20.00 (100.00 - 80.00), NEVER negative
        Wallet updatedWallet = walletRepository.findByUserId(userId).orElseThrow();
        assertEquals(0, new BigDecimal("20.00").compareTo(updatedWallet.getBalance()), "Balance should be 20.00");

        // Verify only 1 escrow record was generated
        assertEquals(1, escrowRepository.count(), "Only one escrow record should exist");
    }
}