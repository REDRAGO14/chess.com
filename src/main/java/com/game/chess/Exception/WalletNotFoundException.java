package com.game.chess.Exception;
// WalletNotFoundException.java (HTTP 404)
public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String message) {
        super(message);
    }
}