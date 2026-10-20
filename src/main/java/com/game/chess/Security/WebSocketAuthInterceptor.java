package com.game.chess.Security;

import com.game.chess.Listener.WebSocketEventListener; // 👈 Import your listener
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final WebSocketEventListener webSocketEventListener; // 👈 Inject listener

    public WebSocketAuthInterceptor(JwtService jwtTokenProvider, UserDetailsService userDetailsService, @Lazy WebSocketEventListener webSocketEventListener) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.webSocketEventListener = webSocketEventListener;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("WebSocket CONNECT missing or malformed Authorization header: {}", authHeader);
                throw new BadCredentialsException("Missing or malformed Authorization header");
            }

            String token = authHeader.substring(7).trim();

            try {
                if (jwtTokenProvider.isTokenValid(token)) {
                    log.error("JWT validation returned false for token: {}", token);
                    throw new BadCredentialsException("Invalid or expired JWT token");
                }

                String username = jwtTokenProvider.extractEmail(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                accessor.setUser(authentication);

                // 🛑 CRITICAL: Cancel the disconnect/forfeit timer upon successful reconnect!
                webSocketEventListener.cancelReconnectTimer(username);

                log.info("WebSocket successfully authenticated and reconnected user: {}", username);

            } catch (Exception e) {
                log.error("WebSocket authentication exception: {}", e.getMessage());
                throw new BadCredentialsException("Invalid or expired JWT token: " + e.getMessage());
            }
        }
        return message;
    }
}