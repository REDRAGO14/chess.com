package com.game.chess.Service;

import com.game.chess.DTO.PaymentInitResponse;
import com.game.chess.DTO.PaymentStatusResponse;
import com.game.chess.Exception.InvalidAmountException;
import com.game.chess.Exception.PaymentGatewayException;
import com.game.chess.Exception.PaymentNotFoundException;
import com.game.chess.Exception.UserNotFound;
import com.game.chess.Model.PaymentStatus;
import com.game.chess.Model.PaymentTransaction;
import com.game.chess.Model.User;
import com.game.chess.Repository.PaymentTransactionRepository;
import com.game.chess.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ChapaPaymentService {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;
    private final String secretKey;
    private final String callbackUrl;
    private final String returnUrl;
    private final PaymentTransactionRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentSettlementService settlementService;

    @Autowired
    public ChapaPaymentService(@Value("${chapa.base-url}") String baseUrl,
                               @Value("${chapa.secret-key}") String secretKey,
                               @Value("${chapa.callback-url}") String callbackUrl,
                               @Value("${chapa.return-url}") String returnUrl,
                               PaymentTransactionRepository paymentRepository,
                               UserRepository userRepository,
                               PaymentSettlementService settlementService) {
        this(buildClient(baseUrl, secretKey), secretKey, callbackUrl, returnUrl,
                paymentRepository, userRepository, settlementService);
    }


    ChapaPaymentService(RestClient restClient, String secretKey, String callbackUrl, String returnUrl,
                        PaymentTransactionRepository paymentRepository, UserRepository userRepository,
                        PaymentSettlementService settlementService) {
        this.restClient = restClient;
        this.secretKey = secretKey;
        this.callbackUrl = callbackUrl;
        this.returnUrl = returnUrl;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.settlementService = settlementService;
    }

    private static RestClient buildClient(String baseUrl, String secretKey) {
        HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(Duration.ofSeconds(10));
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
                .build();
    }


    public PaymentInitResponse initializePayment(Long userId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2)
            throw new InvalidAmountException("Amount must be positive with at most 2 decimals");
        if (secretKey == null || secretKey.isBlank())
            throw new PaymentGatewayException("Chapa is not configured (CHAPA_SECRET_KEY is missing)");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("User " + userId + " not found"));

        String txRef = "chess-" + UUID.randomUUID();
        PaymentTransaction tx = new PaymentTransaction();
        tx.setUser(user);
        tx.setTxRef(txRef);
        tx.setAmount(amount);
        tx.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(tx);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("amount", amount.toPlainString());
        body.put("currency", "ETB");
        body.put("email", user.getEmail());
        body.put("first_name", user.getUserName());
        body.put("tx_ref", txRef);
        body.put("callback_url", callbackUrl);
        body.put("return_url", returnUrl);

        try {
            Map<String, Object> response = restClient.post()
                    .uri("/transaction/initialize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(MAP_TYPE);

            String checkoutUrl = extractCheckoutUrl(response);
            tx.setCheckoutUrl(checkoutUrl);
            paymentRepository.save(tx);
            return new PaymentInitResponse(txRef, checkoutUrl);
        } catch (RestClientException | IllegalStateException e) {
            tx.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(tx);
            throw new PaymentGatewayException("Could not start the payment with Chapa");
        }
    }



    public PaymentStatusResponse confirmPayment(String txRef, Long requesterId) {
        PaymentTransaction tx = paymentRepository.findByTxRef(txRef)
                .orElseThrow(() -> new PaymentNotFoundException("Unknown payment " + txRef));

        if (requesterId != null && !tx.getUser().getId().equals(requesterId))
            throw new PaymentNotFoundException("Unknown payment " + txRef);

        if (tx.getStatus() != PaymentStatus.PENDING)
            return PaymentStatusResponse.from(tx);

        Map<String, Object> data = fetchVerification(txRef);
        if (isSuccessful(txRef, data)) {
            BigDecimal paid = parseAmount(data.get("amount"));
            Object currency = data.get("currency");
            if (paid != null) {
                settlementService.settle(txRef, paid, currency == null ? null : currency.toString());
            }
        }
        PaymentTransaction fresh = paymentRepository.findByTxRef(txRef)
                .orElseThrow(() -> new PaymentNotFoundException("Unknown payment " + txRef));
        return PaymentStatusResponse.from(fresh);
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> fetchVerification(String txRef) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("/transaction/verify/{txRef}", txRef)
                    .retrieve()
                    .body(MAP_TYPE);
            Object data = response == null ? null : response.get("data");
            return data instanceof Map ? (Map<String, Object>) data : null;
        } catch (HttpClientErrorException e) {
            return null;
        } catch (RestClientException e) {
            throw new PaymentGatewayException("Could not verify the payment with Chapa");
        }
    }

    private boolean isSuccessful(String txRef, Map<String, Object> data) {
        if (data == null) return false;
        if (!"success".equalsIgnoreCase(String.valueOf(data.get("status")))) return false;
        Object ref = data.get("tx_ref");
        return ref == null || txRef.equals(String.valueOf(ref));
    }

    private BigDecimal parseAmount(Object value) {
        try { return value == null ? null : new BigDecimal(String.valueOf(value)); }
        catch (NumberFormatException e) { return null; }
    }

    @SuppressWarnings("unchecked")
    private String extractCheckoutUrl(Map<String, Object> response) {
        Object data = response == null ? null : response.get("data");
        Object url = data instanceof Map ? ((Map<String, Object>) data).get("checkout_url") : null;
        if (url == null) throw new IllegalStateException("Chapa did not return a checkout_url");
        return url.toString();
    }
}