package az.azal.skyflow.aircraft.dto;

import az.azal.skyflow.aircraft.model.AircraftStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record AircraftRequest(
		@NotBlank(message = "Registration number is required")
		@Size(min = 2, max = 10, message = "Registration number must be between 2 and 10 characters")
		@Pattern(regexp = "^[A-Z0-9\\-]+$", message = "Registration number must be uppercase letters, numbers, or hyphens")
		String registrationNumber,

		@NotBlank(message = "Model is required")
		@Size(min = 2, max = 100, message = "Model must be between 2 and 100 characters")
		String model,

		@NotNull(message = "Capacity is required")
		@Min(value = 1, message = "Capacity must be at least 1")
		@Max(value = 1000, message = "Capacity must be at most 1000")
		Integer capacity,

		@NotNull(message = "Status is required")
		AircraftStatus status,

		@PastOrPresent(message = "Last maintenance date cannot be in the future")
		LocalDateTime lastMaintenanceDate,

		@NotNull(message = "Next maintenance date is required")
		@Future(message = "Next maintenance date must be in the future")
		LocalDateTime nextMaintenanceDate,

		@NotNull(message = "Total flight hours is required")
		@PositiveOrZero(message = "Total flight hours cannot be negative")
		Long totalFlightHours
) {
}
