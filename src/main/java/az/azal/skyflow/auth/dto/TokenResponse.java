package az.azal.skyflow.auth.dto;

import java.time.LocalDateTime;

public record TokenResponse(
		String accessToken,
		String refreshToken,
		String tokenType,
		LocalDateTime expiresAt
) {
}
