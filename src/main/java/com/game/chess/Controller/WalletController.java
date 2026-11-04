package com.game.chess.Controller;

import com.game.chess.DTO.StakeRequest;
import com.game.chess.DTO.StakeResponse;
import com.game.chess.DTO.WalletResponse;
import com.game.chess.Model.User;
import com.game.chess.Service.MatchEscrowService;
import com.game.chess.Service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final MatchEscrowService escrowService;

    @GetMapping
    public ResponseEntity<WalletResponse> myWallet(@AuthenticationPrincipal User principal) {
        return ResponseEntity.ok(WalletResponse.from(walletService.getOrCreateWallet(principal.getId())));
    }

    @PostMapping("/stake")
    public ResponseEntity<StakeResponse> stake(@AuthenticationPrincipal User principal,
                                               @Valid @RequestBody StakeRequest request) {
        StakeResponse response = escrowService.stakeForMatch(
                request.getGameId(), principal.getId(), request.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}