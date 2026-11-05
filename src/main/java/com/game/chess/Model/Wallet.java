package com.game.chess.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    //We use BigDecimal because it is  exact money math. never double or float.

    @Column(nullable = false, precision = 19, scale = 2)

    private BigDecimal balance = BigDecimal.ZERO;



    @CreationTimestamp
    @Column(name = "created_at", updatable = false)

    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")

    private LocalDateTime updatedAt;




}