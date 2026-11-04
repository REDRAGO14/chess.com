package com.game.chess.Controller;

import com.game.chess.DTO.DepositRequest;
import com.game.chess.DTO.PaymentInitResponse;
import com.game.chess.DTO.PaymentStatusResponse;
import com.game.chess.Model.User;
import com.game.chess.Service.ChapaPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ChapaPaymentService chapaService;

    @PostMapping("/deposit")

    public ResponseEntity<PaymentInitResponse> deposit(@AuthenticationPrincipal User principal,
                                                       @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chapaService.initializePayment(principal.getId(), request.getAmount()));
    }

    @GetMapping("/verify/{txRef}")

    public ResponseEntity<PaymentStatusResponse> verify(@AuthenticationPrincipal User principal,
                                                        @PathVariable String txRef) {
        return ResponseEntity.ok(chapaService.confirmPayment(txRef, principal.getId()));

    }

}