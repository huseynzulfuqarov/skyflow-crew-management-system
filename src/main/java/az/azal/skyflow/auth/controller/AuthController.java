package az.azal.skyflow.auth.controller;

import az.azal.skyflow.auth.dto.LoginRequest;
import az.azal.skyflow.auth.dto.RegisterRequest;
import az.azal.skyflow.auth.dto.TokenResponse;
import az.azal.skyflow.auth.service.AuthService;
import az.azal.skyflow.common.exception.custom.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService service;

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(service.login(request));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/register")
	public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
		service.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refresh(@RequestBody Map<String, String> body) {
		return ResponseEntity.ok(service.refresh(body.get("refreshToken")));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, @RequestBody Map<String, String> body) {
		String accessToken = request.getHeader("Authorization").substring(7);
		String refreshToken = body.get("refreshToken");
		if (refreshToken == null || refreshToken.isBlank()) {
			throw AuthException.missingRefreshToken();
		}
		service.logout(accessToken, refreshToken);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
