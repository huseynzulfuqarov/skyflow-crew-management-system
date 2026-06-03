package az.azal.skyflow.crew.dto;

import az.azal.skyflow.crew.model.CrewRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CrewAssignmentRequest(

		@NotNull(message = "Crew member ID is required")
		UUID crewMemberId,

		@NotNull(message = "Role on flight is required")
		CrewRole roleOnFlight
) {
}
