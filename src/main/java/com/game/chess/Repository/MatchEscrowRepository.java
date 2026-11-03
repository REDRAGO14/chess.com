package com.game.chess.Repository;

import com.game.chess.Model.MatchEscrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchEscrowRepository extends JpaRepository<MatchEscrow, Long> {
    boolean existsByGameIdAndUserId(Long gameId, Long userId);
}