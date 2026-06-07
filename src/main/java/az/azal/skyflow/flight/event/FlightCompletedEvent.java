package az.azal.skyflow.flight.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record FlightCompletedEvent(
        UUID flightId,
        String flightNumber,
        UUID aircraftId,
        LocalDateTime actualArrivalTime,
        String completedBy
) {}