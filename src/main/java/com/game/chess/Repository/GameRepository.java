package com.game.chess.Repository;



import com.game.chess.Model.Game;
import com.game.chess.Model.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT COUNT(g) > 0 FROM Game g WHERE (g.whiteplayer.id = :userId OR g.blackplayer.id = :userId) AND g.status IN :statuses")
    boolean existsActiveGameForUser(@Param("userId") Long userId, @Param("statuses") List<GameStatus> statuses);
}
