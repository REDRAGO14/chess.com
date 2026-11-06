package com.game.chess.Repository;

import com.game.chess.Model.DepositStatus;
import com.game.chess.Model.ManualDeposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// ManualDepositRepository.java
public interface ManualDepositRepository extends JpaRepository<ManualDeposit, Long> {
    List<ManualDeposit> findByStatus(DepositStatus status);
}