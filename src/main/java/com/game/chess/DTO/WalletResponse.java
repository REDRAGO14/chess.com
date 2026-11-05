package com.game.chess.DTO;

import com.game.chess.Model.Wallet;

import java.math.BigDecimal;

public record WalletResponse(Long userId, BigDecimal balance) {


    public static WalletResponse from(Wallet wallet) {

        return new WalletResponse(wallet.getUser().getId(), wallet.getBalance());

    }


}

