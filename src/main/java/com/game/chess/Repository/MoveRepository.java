package com.game.chess.Repository;

import com.game.chess.Model.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoveRepository extends JpaRepository<Move, Long> {

    List<Move> findByGameIdOrderByMoveNumberAsc(Long gameId);
    int countByGameId(Long gameId);
}