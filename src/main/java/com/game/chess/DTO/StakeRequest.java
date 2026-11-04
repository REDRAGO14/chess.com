package com.game.chess.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
public class StakeRequest {

    @NotNull(message = "gameId is required")
    @Positive(message = "gameId must be positive")

    private Long gameId;

    @NotNull(message = "amount is required")

    @DecimalMin(value = "0.01", message = "amount must be at least 0.01")
    @Digits(integer = 17, fraction = 2, message = "amount can have at most 2 decimals")

    private BigDecimal amount;


}