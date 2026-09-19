package com.game.chess.Model;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //this part basically says that the same user can be the white player in d/f games
    @ManyToOne
    @JoinColumn(name = "White_player_id") //keeps track of which user is the white player
    private User whiteplayer;

    @ManyToOne
    @JoinColumn(name = "Black_Player_Id")
    private User blackplayer;

   /* @OneToMany(mappedBy = "game")
    private List<Move> moves = new ArrayList<>();

    (Move) class is empty so cant implement
    this line of code yet
    */

    public Game(User whiteplayer, User blackplayer) {
        this.whiteplayer = whiteplayer;
        this.blackplayer = blackplayer;
    }

}
