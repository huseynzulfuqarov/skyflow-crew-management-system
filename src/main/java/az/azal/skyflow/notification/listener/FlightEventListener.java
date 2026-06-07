package az.azal.skyflow.notification.listener;

import az.azal.skyflow.flight.event.FlightCompletedEvent;
import az.azal.skyflow.flight.event.FlightDelayedEvent;
import az.azal.skyflow.notification.model.NotificationSeverity;
import az.azal.skyflow.notification.model.NotificationType;
import az.azal.skyflow.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlightEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFlightDelayed(FlightDelayedEvent event) {
        log.info("Flight {} delayed by {} minutes", event.flightNumber(), event.delayMinutes());

        notificationService.create(
                NotificationType.FLIGHT_DELAYED,
                event.highRisk() ? NotificationSeverity.CRITICAL : NotificationSeverity.WARNING,
                "Flight " + event.flightNumber() + " delayed",
                "Delayed by " + event.delayMinutes() + " min. Reason: " + event.reason(),
                "FLIGHT",
                event.flightId()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFlightCompleted(FlightCompletedEvent event) {
        log.info("Flight {} completed", event.flightNumber());

        notificationService.create(
                NotificationType.FLIGHT_COMPLETED,
                NotificationSeverity.INFO,
                "Flight " + event.flightNumber() + " landed",
                "Landed at " + event.actualArrivalTime(),
                "FLIGHT",
                event.flightId()
        );
    }
}