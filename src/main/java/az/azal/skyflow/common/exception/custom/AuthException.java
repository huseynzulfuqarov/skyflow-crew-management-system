package az.azal.skyflow.common.exception.custom;

import org.springframework.http.ResponseEntity;

public class AuthException extends RuntimeException {
	public AuthException(String message) {
		super(message);
	}

	public static AuthException invalidToken() {
		return new AuthException("Invalid or expired token");
	}

	public static AuthException invalidTokenType() {
		return new AuthException("Token type mismatch");
	}

	public static AuthException sessionExpired() {
		return new AuthException("Session expired. Please login again.");
	}

	public static AuthException tokenReuseDetected() {
		return new AuthException("Suspicious activity detected. Please login again.");
	}

	public static AuthException invalidCredentials() {
		return new AuthException("Invalid username or password");
	}

	public static AuthException missingRefreshToken() {
		return new AuthException("Refresh token is required");
	}
}
