package az.azal.skyflow.notification.listener;

import az.azal.skyflow.crew.event.CrewStatusChangedEvent;
import az.azal.skyflow.crew.model.CrewStatus;
import az.azal.skyflow.crew.service.FlightCrewAssignmentService;
import az.azal.skyflow.notification.model.NotificationSeverity;
import az.azal.skyflow.notification.model.NotificationType;
import az.azal.skyflow.notification.service.NotificationService;
import az.azal.skyflow.crew.repository.CrewMemberRepository;
import az.azal.skyflow.crew.model.CrewMember;
import az.azal.skyflow.common.exception.custom.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrewEventListener {

    private final FlightCrewAssignmentService flightCrewAssignmentService;
    private final NotificationService notificationService;
    private final CrewMemberRepository crewMemberRepository;

    @EventListener
    public void onCrewUnavailable(CrewStatusChangedEvent event) {
        if (event.newStatus() == CrewStatus.SICK
                || event.newStatus() == CrewStatus.ON_LEAVE
                || event.newStatus() == CrewStatus.INACTIVE) {

            log.info("Crew {} is now {}, removing future assignments",
                    event.employeeId(), event.newStatus());

            CrewMember crewMember = crewMemberRepository.findById(event.crewMemberId())
                    .orElseThrow(() -> ResourceNotFoundException.byId("CrewMember", event.crewMemberId()));

            flightCrewAssignmentService.handleCrewUnavailability(crewMember);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCrewStatusChangedNotification(CrewStatusChangedEvent event) {
        log.info("Crew {} status changed: {} -> {}",
                event.employeeId(), event.oldStatus(), event.newStatus());

        notificationService.create(
                NotificationType.CREW_STATUS_CHANGED,
                event.newStatus() == CrewStatus.SICK ? NotificationSeverity.WARNING : NotificationSeverity.INFO,
                "Crew " + event.employeeId() + " status changed",
                "Status: " + event.oldStatus() + " → " + event.newStatus(),
                "CREW",
                event.crewMemberId()
        );
    }
}