package com.game.chess.Model;

import com.game.chess.Config.ChessConstants;
import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //this part basically says that the same user can be the white player in d/f games
    @ManyToOne(optional = false)
    @JoinColumn(name = "White_player_id", nullable = false) //keeps track of which user is the white player
    private User whiteplayer;

    @ManyToOne
    @JoinColumn(name = "Black_Player_Id")
    private User blackplayer;

   /* @OneToMany(mappedBy = "game")
    private List<Move> moves = new ArrayList<>();

    (Move) class is empty so cant implement
    this line of code yet
    */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameStatus status = GameStatus.WAITING;

    @Column(nullable = false, length = 100)
    private String fen = ChessConstants.STRING_FEN;

    @CreationTimestamp
    @Column(name = "created_At", updatable = false)
    private LocalDateTime createdAt;

}
