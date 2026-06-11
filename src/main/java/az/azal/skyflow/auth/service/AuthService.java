package az.azal.skyflow.auth.service;

import az.azal.skyflow.auth.dto.LoginRequest;
import az.azal.skyflow.auth.dto.RegisterRequest;
import az.azal.skyflow.auth.dto.TokenResponse;

public interface AuthService {

	 TokenResponse login(LoginRequest request);

	 void register(RegisterRequest request);

	 void logout(String accessToken, String refreshToken);

	 TokenResponse refresh(String refreshToken);

}
