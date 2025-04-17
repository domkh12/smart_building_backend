package edu.npic.smartBuilding.config;

import edu.npic.smartBuilding.features.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String roomId = headerAccessor.getFirstNativeHeader("BUILDING_ROOM_ID");
        log.info("Received Headers: {}", headerAccessor.getFirstNativeHeader("BUILDING_ROOM_ID"));

        if (roomId == null) {
            roomId = "unknown_room";
        }

        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("roomId", roomId);

        log.info("New WebSocket connection established: sessionId={}, roomId={}", sessionId, roomId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        // Retrieve stored roomId from session attributes
        String roomId = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).remove("roomId");

        if (roomId != null) {
            log.info("WebSocket disconnected: sessionId={}, roomId={}", sessionId, roomId);
            performActionOnDisconnect(roomId);
        } else {
            log.warn("WebSocket disconnected but session data not found: sessionId={}", sessionId);
        }
    }

    private void performActionOnDisconnect(String roomId) {
        // Custom logic for when a user/device disconnects
        log.info("Performing action for disconnected room: {}", roomId);
        // Example: Send alert, update database, etc.
    }

}
