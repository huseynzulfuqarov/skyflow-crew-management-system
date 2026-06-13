package az.azal.skyflow.crew.controller;

import az.azal.skyflow.crew.dto.CrewResponse;
import az.azal.skyflow.crew.dto.CrewStatusRequest;
import az.azal.skyflow.crew.service.CrewStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crew")
@RequiredArgsConstructor
public class CrewStatusController {

	private final CrewStatusService crewStatusService;

	@PreAuthorize("hasRole('OPERATIONS')")
	@PatchMapping(path = "/{id}/status")
	public ResponseEntity<CrewResponse> updateCrewStatus(@PathVariable UUID id, @Valid @RequestBody CrewStatusRequest request, @AuthenticationPrincipal String updatedBy) {
		return ResponseEntity.ok(crewStatusService
				.updateCrewStatus(id, request.newStatus(), updatedBy));
	}
}
