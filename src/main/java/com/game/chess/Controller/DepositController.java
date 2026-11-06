package com.game.chess.Controller;

import com.game.chess.DTO.ManualDepositResponse;
import com.game.chess.Model.ManualDeposit;
import com.game.chess.Model.User;
import com.game.chess.Service.ManualDepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final ManualDepositService manualDepositService;

    @PostMapping(value = "/manual", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ManualDepositResponse> submitManualDeposit(
            @AuthenticationPrincipal User principal,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("file") MultipartFile file) {

        ManualDeposit deposit = manualDepositService.submitDeposit(principal.getId(), amount, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ManualDepositResponse(
                        deposit.getId(),
                        deposit.getStatus(),
                        "Deposit received. Status: " + deposit.getStatus()
                ));
    }
}