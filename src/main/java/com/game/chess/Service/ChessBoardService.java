package com.game.chess.Service;

import com.game.chess.Model.Game;
import com.game.chess.Repository.GameRepository;
import com.github.bhlangonijr.chesslib.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChessBoardService {

    private final GameRepository gameRepository;



}