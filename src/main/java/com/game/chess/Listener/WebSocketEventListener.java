package com.game.chess.Listener;

import com.game.chess.Model.Game;
import com.game.chess.Model.GameStatus;
import com.game.chess.Repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final GameRepository gameRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Stores active disconnect timers for players (Key: username, Value: ScheduledFuture)
    private final ConcurrentMap<String, ScheduledFuture<?>> disconnectTimers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();

        if (principal == null) {
            return;
        }

        String username = principal.getName();
        log.info("User disconnected from WebSocket: {}", username);

        // Find active games involving this user as white or black player
        // (Assuming you have a repository query or can fetch active games)
        gameRepository.findAll().stream()
                .filter(game -> game.getStatus() == GameStatus.ACTIVE)
                .filter(game -> (game.getWhiteplayer() != null && game.getWhiteplayer().getUserName().equals(username)) ||
                        (game.getBlackplayer() != null && game.getBlackplayer().getUserName().equals(username)))
                .forEach(game -> startReconnectTimer(game, username));
    }

    private void startReconnectTimer(Game game, String username) {
        log.info("Starting 30-second reconnect timer for user {} in game ID {}", username, game.getId());

        // Schedule a 30-second countdown task
        ScheduledFuture<?> timerTask = scheduler.schedule(() -> {
            synchronized (this) {
                Game currentFreshGame = gameRepository.findById(game.getId()).orElse(null);

                if (currentFreshGame != null && currentFreshGame.getStatus() == GameStatus.ACTIVE) {
                    currentFreshGame.setStatus(GameStatus.FINISHED);
                    gameRepository.save(currentFreshGame);

                    log.info("Player {} failed to reconnect within 30s. Forfeiting game ID {}", username, game.getId());

                    // Broadcast forfeiture / timeout to all clients watching this game
                    messagingTemplate.convertAndSend("/topic/game/" + game.getId(),
                            "Game Over: Player " + username + " disconnected and ran out of reconnection time.");
                }
            }
        }, 30, TimeUnit.SECONDS);

        disconnectTimers.put(username, timerTask);
    }

    /**
     * Call this method inside your WebSocket CONNECT or subscription handler
     * if a player reconnects within the 30-second window to cancel the forfeiture timer.
     */
    public void cancelReconnectTimer(String username) {
        ScheduledFuture<?> task = disconnectTimers.remove(username);
        if (task != null) {
            task.cancel(true);
            log.info("Player {} reconnected successfully. Disconnect timer canceled.", username);
        }
    }
}