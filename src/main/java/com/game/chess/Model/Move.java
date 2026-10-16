package com.game.chess.Model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(
      name = "moves",
      uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "move_number"})
)
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    @Column(name = "move_number", nullable = false)
    private Integer moveNumber;
    @Column(name = "from_square", nullable = false, length=2)
    private String fromSquare;

    @Column(name = "to_square", nullable = false, length = 2)
    private String toSquare;
    @Column(name = "piece_moved", nullable= false, length=20)
    private String pieceMoved;

    @Column(name = "captured_piece", length = 20)
    private String capturedPiece;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}
