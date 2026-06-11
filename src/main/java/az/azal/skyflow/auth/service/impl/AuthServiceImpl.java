package az.azal.skyflow.auth.service.impl;

import az.azal.skyflow.auth.dto.JwtProperties;
import az.azal.skyflow.auth.dto.LoginRequest;
import az.azal.skyflow.auth.dto.RegisterRequest;
import az.azal.skyflow.auth.dto.TokenResponse;
import az.azal.skyflow.auth.model.AppUser;
import az.azal.skyflow.auth.repository.AppUserRepository;
import az.azal.skyflow.auth.service.AuthService;
import az.azal.skyflow.auth.service.JwtService;
import az.azal.skyflow.common.exception.custom.AuthException;
import az.azal.skyflow.common.exception.custom.DuplicateResourceException;
import az.azal.skyflow.common.exception.custom.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AppUserRepository appUserRepository;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtProperties jwtProperties;
	private final StringRedisTemplate redisBlacklist;


	@Override
	public TokenResponse login(LoginRequest request) {

		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

		AppUser user = appUserRepository.findByUsername(request.username()).orElseThrow(() -> ResourceNotFoundException.byField("User", "username", request.username()));

		return createTokenPair(user, UUID.randomUUID());
	}

	@Override
	@Transactional
	public void register(RegisterRequest request) {

		if(appUserRepository.existsByUsername(request.username())) {
			throw DuplicateResourceException.byField("User", "username", request.username());
		}
		if(appUserRepository.existsByEmail(request.email())) {
			throw DuplicateResourceException.byField("User", "email", request.email());
		}

		AppUser user = new AppUser();
		user.setUsername(request.username());
		user.setEmail(request.email());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setFullName(request.fullName());
		user.setRole(request.role());
		user.setActive(true);
		appUserRepository.save(user);

	}

	@Override
	public void logout(String accessToken, String refreshToken) {
		String jti = jwtService.extractClaim(accessToken, "jti");

		long ttl = jwtService.extractExpiration(accessToken).getTime() - System.currentTimeMillis();

		if(ttl > 0){
			redisBlacklist.opsForValue().set("blacklist:" + jti,"revoked", ttl, TimeUnit.MILLISECONDS);
		}

		String familyId = jwtService.extractClaim(refreshToken, "fid");
		redisBlacklist.delete("refresh_family:" + familyId);
	}

	@Override
	public TokenResponse refresh(String refreshToken) {

			if(!jwtService.isTokenValid(refreshToken)) {
				throw AuthException.invalidToken();
			}

			String typ = jwtService.extractClaim(refreshToken, "typ");
			if(!typ.equals("REFRESH")) {
				throw AuthException.invalidTokenType();
			}

			String familyId = jwtService.extractClaim(refreshToken, "fid");
			String jti = jwtService.extractClaim(refreshToken, "jti");

			if(!Boolean.TRUE.equals(redisBlacklist.hasKey("refresh_family:" + familyId))) {
				throw AuthException.sessionExpired();
			}
			String currentJti = (String) redisBlacklist.opsForHash().get("refresh_family:" + familyId, "currentJti");

			if(!jti.equals(currentJti)) {
				redisBlacklist.delete("refresh_family:" + familyId);
				throw AuthException.tokenReuseDetected();
			}

			String username = jwtService.extractUsername(refreshToken);
			AppUser user = appUserRepository.findByUsername(username)
					.orElseThrow(() -> ResourceNotFoundException.byField("User", "username", username));

			return createTokenPair(user, UUID.randomUUID());

	}

	private TokenResponse createTokenPair(AppUser user, UUID familyId) {
		String accessToken = jwtService.generateAccessToken(user);
		String refreshToken = jwtService.generateRefreshToken(user, familyId);

		String refreshJti = jwtService.extractClaim(refreshToken, "jti");
		String redisKey = "refresh_family:" + familyId;
		redisBlacklist.opsForHash().put(redisKey, "currentJti", refreshJti);
		redisBlacklist.opsForHash().put(redisKey, "username", user.getUsername());
		redisBlacklist.expire(redisKey, jwtProperties.refreshTokenExpiration(), TimeUnit.MILLISECONDS);

		return new TokenResponse(
				accessToken,
				refreshToken,
				"Bearer",
				jwtService.extractExpiration(accessToken).getTime() / 1000
		);
	}
}
