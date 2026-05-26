package az.azal.skyflow.crew.dto;

import az.azal.skyflow.crew.model.CrewRole;
import jakarta.validation.constraints.*;

public record CrewRequest(
		@NotBlank(message = "Employee ID is required")
		@Size(max = 20, message = "Employee ID must be at most 20 characters")
		@Pattern(regexp = "^[A-Z0-9\\-]+$", message = "Employee ID must be uppercase letters, numbers, or hyphens")
		String employeeId,

		@NotBlank(message = "First name is required")
		@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
		String firstName,

		@NotBlank(message = "Last name is required")
		@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
		String lastName,

		@NotBlank(message = "Email is required")
		@Email(message = "Invalid email format")
		String email,

		@NotBlank(message = "Phone number is required")
		@Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Invalid phone number format")
		String phoneNumber,

		@NotNull(message = "Role is required")
		CrewRole role
) {
}
