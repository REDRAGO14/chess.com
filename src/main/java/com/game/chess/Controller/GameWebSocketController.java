package com.game.chess.Controller;

import com.game.chess.DTO.MoveRequest;
import com.game.chess.DTO.MoveResponse;
import com.game.chess.Service.MoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final MoveService moveService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/{gameId}/move")
    public void makeMove(@DestinationVariable Long gameId, @Payload MoveRequest request) {
        // 1. Process move using existing service logic (validation, FEN update, persistence)
        MoveResponse response = moveService.createMove(request);

        // 2. Broadcast the move update to all clients subscribed to this game room
        messagingTemplate.convertAndSend("/topic/game/" + gameId, response);
    }
}