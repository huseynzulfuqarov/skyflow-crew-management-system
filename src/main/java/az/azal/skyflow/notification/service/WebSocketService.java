package az.azal.skyflow.notification.service;

import az.azal.skyflow.notification.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastNotification(NotificationResponse notification) {
        log.info("Sending real-time notification via WebSocket: {}", notification.title());

        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
