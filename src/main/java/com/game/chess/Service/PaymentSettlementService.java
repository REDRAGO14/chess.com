package com.game.chess.Service;

import com.game.chess.Exception.PaymentNotFoundException;
import com.game.chess.Model.PaymentStatus;
import com.game.chess.Model.PaymentTransaction;
import com.game.chess.Repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentSettlementService {

    private final PaymentTransactionRepository paymentRepository;
    private final WalletService walletService;

    @Transactional
    public void settle(String txRef, BigDecimal paidAmount, String paidCurrency) {

        PaymentTransaction tx = paymentRepository.findByTxRefForUpdate(txRef)     // row lock
                .orElseThrow(() -> new PaymentNotFoundException("Unknown payment " + txRef));

        if (tx.getStatus() != PaymentStatus.PENDING) return;

        boolean amountOk = paidAmount != null && paidAmount.compareTo(tx.getAmount()) == 0;
        boolean currencyOk = paidCurrency == null || paidCurrency.equalsIgnoreCase(tx.getCurrency());
        if (!amountOk || !currencyOk) {
            tx.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(tx);
            return;
        }


        walletService.credit(tx.getUser().getId(), tx.getAmount(), "CHAPA:" + txRef);

        tx.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(tx);
    }


}