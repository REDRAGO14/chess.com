package com.game.chess.Repository;

import com.game.chess.Model.GameMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoveRepository extends JpaRepository<GameMove, Long> {

    List<GameMove> findByGameIdOrderByMoveNumberAsc(Long gameId);
    int countByGameId(Long gameId);
}