package com.game.chess.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "wallet_audit_log")
public class WalletAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)

    private TransactionType type;



    @Column(nullable = false, precision = 19, scale = 2)

    private BigDecimal amount;


    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)

    private BigDecimal balanceAfter;


    @Column(nullable = false, length = 100)
    private String reference;

    @CreationTimestamp

    @Column(name = "created_at", updatable = false)

    private LocalDateTime createdAt;




}