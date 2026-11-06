package com.game.chess.Controller;

import com.game.chess.Model.ManualDeposit;
import com.game.chess.Model.User;
import com.game.chess.Model.DepositStatus;
import com.game.chess.Service.AdminDepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/deposits")
@RequiredArgsConstructor
public class AdminDepositController {

    private final AdminDepositService adminDepositService;

    @GetMapping
    public ResponseEntity<List<ManualDeposit>> listPendingDeposits(
            @RequestParam(required = false, defaultValue = "PENDING_ADMIN_REVIEW") DepositStatus status) {
        List<ManualDeposit> deposits = adminDepositService.getDepositsByStatus(status);
        return ResponseEntity.ok(deposits);
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<Resource> viewReceipt(@PathVariable Long id) {
        Resource file = adminDepositService.getReceiptFile(id);
        // Default to JPEG/PNG stream; file path is sourced entirely from database record, never user params
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(file);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ManualDeposit> approveDeposit(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin) {
        ManualDeposit updated = adminDepositService.approveDeposit(id, admin.getId());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ManualDeposit> rejectDeposit(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin,
            @RequestBody(required = false) Map<String, String> payload) {
        String note = payload != null ? payload.getOrDefault("adminNote", "No reason provided") : "No reason provided";
        ManualDeposit updated = adminDepositService.rejectDeposit(id, admin.getId(), note);
        return ResponseEntity.ok(updated);
    }
}
