package az.azal.skyflow.aircraft.dto;

import az.azal.skyflow.aircraft.model.AircraftStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AircraftResponse(
		UUID id,
		String registrationNumber,
		String model,
		Integer capacity,
		AircraftStatus status,
		LocalDateTime lastMaintenanceDate,
		LocalDateTime nextMaintenanceDate,
		Long totalFlightHours,
		LocalDateTime createdAt) {
}
