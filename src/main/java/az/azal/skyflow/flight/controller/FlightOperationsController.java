package az.azal.skyflow.flight.controller;

import az.azal.skyflow.flight.dto.DelayRequest;
import az.azal.skyflow.flight.dto.DelayResponse;
import az.azal.skyflow.flight.dto.FlightResponse;
import az.azal.skyflow.flight.service.FlightCompletionService;
import az.azal.skyflow.flight.service.FlightDelayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightOperationsController {

	private final FlightDelayService flightDelayService;
	private final FlightCompletionService flightCompletionService;

	@PreAuthorize("hasRole('OPERATIONS')")
	@PostMapping("/{id}/delay")
	public ResponseEntity<DelayResponse> delayFlight(@PathVariable UUID id, @Valid @RequestBody DelayRequest request, @AuthenticationPrincipal String delayedBy) {
		return ResponseEntity.status(HttpStatus.CREATED).body(flightDelayService.delayFlight(id, request, delayedBy));
	}

	@PreAuthorize("hasRole('OPERATIONS')")
	@PostMapping("/{id}/complete")
	public ResponseEntity<FlightResponse> completeFlight(@PathVariable UUID id, @AuthenticationPrincipal String completedBy){
		return ResponseEntity.ok(flightCompletionService.completeFlight(id, completedBy));
	}

}
