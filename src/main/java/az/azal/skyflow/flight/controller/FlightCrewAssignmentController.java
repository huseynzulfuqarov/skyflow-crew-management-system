package az.azal.skyflow.flight.controller;


import az.azal.skyflow.crew.dto.CrewAssignmentRequest;
import az.azal.skyflow.crew.dto.CrewAssignmentResponse;
import az.azal.skyflow.crew.service.FlightCrewAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/flight-crew-assignments")
public class FlightCrewAssignmentController {

	private final FlightCrewAssignmentService assignmentService;

	@PostMapping("/assign/{flightId}")
	public ResponseEntity<CrewAssignmentResponse> assignCrewToFlight(@PathVariable("flightId") UUID flightId, CrewAssignmentRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.assignCrewToFlights(flightId, request));
	}

}
