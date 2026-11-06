package com.game.chess.Controller;

import com.game.chess.DTO.BankVerifyRequest;
import com.game.chess.DTO.BankVerifyResponse;
import com.game.chess.Model.VerificationResult;
import com.game.chess.Service.ResilientVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/dev/bank-verification")
@ConditionalOnProperty(name = "bank.verification.test-endpoint", havingValue = "true")
@RequiredArgsConstructor


public class BankVerificationTestController {

    private final ResilientVerificationService verificationService;

    @PostMapping
    public BankVerifyResponse verify(@Valid @RequestBody BankVerifyRequest request) {
        long start = System.nanoTime();
        VerificationResult result = verificationService.verify(request.getBankReference(), request.getAmount());
        long millis = (System.nanoTime() - start) / 1_000_000;
        return new BankVerifyResponse(result, verificationService.circuitState(), millis);
    }

    @GetMapping("/state")
    public Map<String, String> state() {
        return Map.of("circuitState", verificationService.circuitState());
    }
}