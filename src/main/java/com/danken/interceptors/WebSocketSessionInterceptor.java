package com.danken.interceptors;

import java.util.Optional;

import com.danken.LabowletState;
import com.danken.sessions.GameSession;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketSessionInterceptor implements ChannelInterceptor {

    private final LabowletState state;

    public WebSocketSessionInterceptor() {
        this.state = LabowletState.getInstance();
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            final var session = Optional.ofNullable(accessor.getNativeHeader("x-auth-token")).map(list -> list.get(0)).map(state::getSessionById).orElse(null);
            final GameSession gameSession = state.getGameSessionFromSession(session);
            accessor.getSessionAttributes().put("session", gameSession);
        }
        return message;
    }
}
