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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightOperationsController {

	private final FlightDelayService flightDelayService;
	private final FlightCompletionService flightCompletionService;

	@PostMapping("/{id}/delay")
	public ResponseEntity<DelayResponse> delayFlight(@PathVariable UUID id, @Valid @RequestBody DelayRequest request){
		return ResponseEntity.status(HttpStatus.CREATED).body(flightDelayService.delayFlight(id, request, "system"));
	}

	@PostMapping("/{id}/complete")
	public ResponseEntity<FlightResponse> completeFlight(@PathVariable UUID id){
		return ResponseEntity.ok(flightCompletionService.completeFlight(id, "system"));
	}

}
