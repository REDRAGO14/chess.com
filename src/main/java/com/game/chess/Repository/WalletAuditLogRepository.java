package com.game.chess.Repository;

import com.game.chess.Model.TransactionType;
import com.game.chess.Model.WalletAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletAuditLogRepository extends JpaRepository<WalletAuditLog, Long> {
    boolean existsByTypeAndReference(TransactionType type, String reference);
}