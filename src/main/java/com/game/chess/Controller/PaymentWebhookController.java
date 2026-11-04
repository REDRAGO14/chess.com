package com.game.chess.Controller;

import com.game.chess.DTO.PaymentStatusResponse;
import com.game.chess.Exception.PaymentNotFoundException;
import com.game.chess.Service.ChapaPaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentWebhookController {

    private final ChapaPaymentService chapaService;
    private final String webhookSecret;

    public PaymentWebhookController(ChapaPaymentService chapaService,

                                    @Value("${chapa.webhook-secret}") String webhookSecret) {
        this.chapaService = chapaService;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "x-chapa-signature", required = false) String payloadSignature,
            @RequestHeader(value = "chapa-signature", required = false) String secretSignature) {


        if (!isFromChapa(rawBody, payloadSignature, secretSignature)) {
            return ResponseEntity.status(401).build();
        }

        Object txRef;
        try {
            Map<String, Object> payload = JsonParserFactory.getJsonParser().parseMap(rawBody);
            txRef = payload.get("tx_ref");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
        if (txRef == null) return ResponseEntity.ok().build();


        try {
            chapaService.confirmPayment(txRef.toString(), null);
        } catch (PaymentNotFoundException e) {

        }
        return ResponseEntity.ok().build();
    }


    @GetMapping("/callback")
    public ResponseEntity<PaymentStatusResponse> callback(
            @RequestParam(name = "trx_ref", required = false) String trxRef,
            @RequestParam(name = "tx_ref", required = false) String txRef) {
        String ref = trxRef != null ? trxRef : txRef;
        if (ref == null || ref.isBlank()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(chapaService.confirmPayment(ref, null));
    }


    private boolean isFromChapa(String rawBody, String payloadSignature, String secretSignature) {
        if (webhookSecret == null || webhookSecret.isBlank()) return false;   // never accept when not configured
        return matches(payloadSignature, hmac(rawBody)) || matches(secretSignature, hmac(webhookSecret));
    }

    private String hmac(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC is not available", e);
        }
    }

    private boolean matches(String received, String expected) {
        if (received == null) return false;
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                received.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
    }
}