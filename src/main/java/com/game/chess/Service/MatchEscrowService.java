package com.game.chess.Service;

import com.game.chess.DTO.StakeResponse;
import com.game.chess.Exception.GameNotFoundException;
import com.game.chess.Exception.InvalidGameStateException;
import com.game.chess.Exception.NotAGameParticipantException;
import com.game.chess.Model.EscrowStatus;
import com.game.chess.Model.Game;
import com.game.chess.Model.GameStatus;
import com.game.chess.Model.MatchEscrow;
import com.game.chess.Model.TransactionType;
import com.game.chess.Model.Wallet;
import com.game.chess.Repository.GameRepository;
import com.game.chess.Repository.MatchEscrowRepository;
import com.game.chess.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MatchEscrowService {

    private final GameRepository gameRepository;
    private final MatchEscrowRepository escrowRepository;
    private final WalletService walletService;
    private final UserRepository userRepository;




    @Transactional
    public StakeResponse stakeForMatch(Long gameId, Long userId, BigDecimal amount) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game " + gameId + " not found"));

        boolean isWhite = game.getWhiteplayer().getId().equals(userId);
        boolean isBlack = game.getBlackplayer() != null && game.getBlackplayer().getId().equals(userId);

        if (!isWhite && !isBlack) {
            throw new NotAGameParticipantException("You are not a player in game " + gameId);
        }


        if (game.getStatus() == GameStatus.ACTIVE || game.getStatus() == GameStatus.FINISHED) {
            throw new InvalidGameStateException("Stake before the game starts (status: " + game.getStatus() + ")");
        }


        if (escrowRepository.existsByGameIdAndUserId(gameId, userId)) {
            throw new InvalidGameStateException("You already staked for game " + gameId);
        }

        Wallet wallet = walletService.debit(userId, amount, TransactionType.STAKE,
                "GAME:" + gameId + ":USER:" + userId);



        MatchEscrow escrow = new MatchEscrow();
        escrow.setGame(game);
        escrow.setUser(userRepository.getReferenceById(userId));
        escrow.setAmount(amount);
        escrow.setStatus(EscrowStatus.LOCKED);
        MatchEscrow saved = escrowRepository.save(escrow);

        return new StakeResponse(saved.getId(), gameId, amount, saved.getStatus(), wallet.getBalance());


    }


}