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
@Table(name = "match_escrow")
public class MatchEscrow {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)


    @JoinColumn(name = "game_id", nullable = false)
    private Game game;



    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)

    private User user;

    @Column(nullable = false, precision = 19, scale = 2)

    private BigDecimal amount;


    @Enumerated(EnumType.STRING)

    @JdbcTypeCode(SqlTypes.VARCHAR)

    @Column(nullable = false, length = 20)

    private EscrowStatus status = EscrowStatus.LOCKED;




    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;



}