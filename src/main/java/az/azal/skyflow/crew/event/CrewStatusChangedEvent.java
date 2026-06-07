package az.azal.skyflow.crew.event;

import az.azal.skyflow.crew.model.CrewStatus;

import java.util.UUID;

public record CrewStatusChangedEvent(
        UUID crewMemberId,
        String employeeId,
        CrewStatus oldStatus,
        CrewStatus newStatus,
        String changedBy
) {}