package com.game.chess.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ManualDeposit.java
@Data
@NoArgsConstructor
@Entity
@Table(name = "manual_deposits")
public class ManualDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "bank_reference")
    private String bankReference; // Generated internally since user doesn't type it

    @Column(name = "receipt_file", nullable = false)
    private String receiptFile; // The stored UUID filename

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DepositStatus status = DepositStatus.PENDING;

    @Column(name = "admin_note")
    private String adminNote;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Version
    private Long version; // Optimistic locking guard

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}