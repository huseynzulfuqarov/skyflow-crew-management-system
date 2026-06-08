package az.azal.skyflow.notification.service;

import az.azal.skyflow.common.exception.custom.ResourceNotFoundException;
import az.azal.skyflow.notification.dto.NotificationResponse;
import az.azal.skyflow.notification.mapper.NotificationMapper;
import az.azal.skyflow.notification.model.Notification;
import az.azal.skyflow.notification.model.NotificationSeverity;
import az.azal.skyflow.notification.model.NotificationType;
import az.azal.skyflow.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final WebSocketService webSocketService;

    @Transactional
    public NotificationResponse create(
            NotificationType type, NotificationSeverity severity, String title,
            String message, String targetType, UUID targetId) {

        Notification notification = new Notification();

        notification.setType(type);
        notification.setSeverity(severity);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        notification.setRead(false);
        notificationRepository.save(notification);

        NotificationResponse response = notificationMapper.toResponse(notification);

        webSocketService.broadcastNotification(response);

        return response;
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUnreadNotifications(Pageable pageable) {
        return notificationRepository.findByIsReadFalseOrderByCreatedAtDesc(pageable)
                .map(notificationMapper::toResponse);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ResourceNotFoundException.byId("Notification", notificationId));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead() {
        notificationRepository.markAllAsRead();
    }
}