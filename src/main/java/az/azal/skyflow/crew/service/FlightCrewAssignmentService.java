package az.azal.skyflow.crew.service;

import az.azal.skyflow.crew.dto.CrewAssignmentRequest;
import az.azal.skyflow.crew.dto.CrewAssignmentResponse;

import java.util.UUID;

public interface FlightCrewAssignmentService {

	CrewAssignmentResponse assignCrewToFlights(UUID flightId, CrewAssignmentRequest request);
}
