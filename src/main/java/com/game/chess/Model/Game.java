package com.game.chess.Model;



import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //this part basically says that the same user can be the white player in d/f games
    @ManyToOne
    @JoinColumn(name = "White_player_id") //keeps track of which user is the white player
    private User whiteplayer;

    @ManyToOne
    @JoinColumn(name = "Black_Player_Id")
    private User blackplayer;


    @CreationTimestamp
    private LocalDateTime createdAt;
}