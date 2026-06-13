package az.azal.skyflow.crew.dto;

import az.azal.skyflow.crew.model.CrewStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrewStatusRequest(

		@NotNull(message = "New status must not be null")
		CrewStatus newStatus,

		@Size(max = 500, message = "Reason must not exceed 500 characters")
		String reason
) {
}
