package com.game.chess.Model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity @Table(name = "moves")
public class GameMove {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id")          // foreign key to games
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id")        // foreign key to users
    private User player;
    @Column(name = "piece_moved", length = 20)
    private String pieceMoved; // Required by Biruk's service

    @Column(name = "captured_piece", length = 20)
    private String capturedPiece; // Required by Biruk's service
    private int moveNumber;                // 1, 2, 3 ... (ply count)
    private String san;                    // "e4", "Nf3", "Qxf7#"
    private String fromSquare;             // "e2"
    private String toSquare;               // "e4"
    private LocalDateTime createdAt = LocalDateTime.now();
}
