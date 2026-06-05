package az.azal.skyflow.flight.service;

import az.azal.skyflow.flight.dto.FlightResponse;

import java.util.UUID;

public interface FlightCompletionService {

	FlightResponse completeFlight(UUID flightId, String completedBy);
}
