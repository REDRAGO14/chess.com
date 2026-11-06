package com.game.chess.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

// ManualDepositRequest.java
public record ManualDepositRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Minimum deposit amount is 1.00")
        BigDecimal amount,

        @NotNull(message = "Receipt file is required")
        MultipartFile file
) {}