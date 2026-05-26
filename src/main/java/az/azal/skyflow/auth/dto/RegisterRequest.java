package az.azal.skyflow.auth.dto;

import az.azal.skyflow.auth.model.UserRole;
import jakarta.validation.constraints.*;

public record RegisterRequest(

		@NotBlank(message = "Username is required")
		@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
		@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
		String username,

		@NotBlank(message = "Email is required")
		@Email(message = "Invalid email format")
		String email,

		@NotBlank(message = "Password is required")
		@Size(min = 8, message = "Password must be at least 8 characters")
		@Pattern(
				regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
				message = "Password must contain uppercase, lowercase, number and special character"
		)
		String password,

		@NotBlank(message = "Full name is required")
		@Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
		String fullName,

		@NotNull(message = "Role is required")
		UserRole role
) {
}
