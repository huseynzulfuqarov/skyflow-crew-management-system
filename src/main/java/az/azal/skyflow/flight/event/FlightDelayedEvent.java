package az.azal.skyflow.flight.event;

import az.azal.skyflow.flight.model.DelayReason;

import java.time.LocalDateTime;
import java.util.UUID;

public record FlightDelayedEvent(
        UUID flightId,
        String flightNumber,
        DelayReason reason,
        int delayMinutes,
        boolean highRisk,
        LocalDateTime newDepartureTime,
        LocalDateTime newArrivalTime,
        String delayedBy
) {}