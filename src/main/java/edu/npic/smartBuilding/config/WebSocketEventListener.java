package edu.npic.smartBuilding.config;

import edu.npic.smartBuilding.features.user.UserServiceImpl;
import edu.npic.smartBuilding.features.user.dto.IsOnlineRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final UserServiceImpl userServiceImpl;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = accessor.getFirstNativeHeader("id");
        if (userId != null) {
            Objects.requireNonNull(accessor.getSessionAttributes()).put("userId", userId);
            userServiceImpl.connectedUsers(Integer.valueOf(userId), IsOnlineRequest.builder().isOnline(true).build());
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("Received a new web socket disconnect event: {}", event.getMessage());

        // Retrieve userId from session attributes
        Object userIdObj = accessor.getSessionAttributes() != null
                ? accessor.getSessionAttributes().get("userId")
                : null;

        if (userIdObj != null) {
            String userId = userIdObj.toString();
            log.info("User disconnected: {}", userId);
            userServiceImpl.connectedUsers(Integer.valueOf(userId), IsOnlineRequest.builder().isOnline(false).build());
        } else {
            log.warn("No userId found in session attributes during disconnect.");
        }
    }

}
