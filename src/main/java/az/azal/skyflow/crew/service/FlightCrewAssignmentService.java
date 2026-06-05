package az.azal.skyflow.crew.service;

import az.azal.skyflow.crew.dto.CrewAssignmentRequest;
import az.azal.skyflow.crew.dto.CrewAssignmentResponse;
import az.azal.skyflow.crew.model.CrewMember;
import az.azal.skyflow.crew.model.FlightCrewAssignment;

import java.util.List;
import java.util.UUID;

public interface FlightCrewAssignmentService {

	CrewAssignmentResponse assignCrewToFlights(UUID flightId, CrewAssignmentRequest request);

	List<FlightCrewAssignment> handleCrewUnavailability(CrewMember crewMember);
}
