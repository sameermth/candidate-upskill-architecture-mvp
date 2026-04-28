package com.candidateupskill.backend.identity.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.candidateupskill.backend.identity.api.AuthSessionResponse;
import com.candidateupskill.backend.identity.api.LoginRequest;
import com.candidateupskill.backend.identity.api.SignupRequest;
import com.candidateupskill.backend.identity.api.UserProfileResponse;
import com.candidateupskill.backend.identity.domain.RefreshToken;
import com.candidateupskill.backend.identity.domain.UserAccount;
import com.candidateupskill.backend.identity.domain.UserStatus;
import com.candidateupskill.backend.identity.repository.RefreshTokenRepository;
import com.candidateupskill.backend.identity.repository.UserAccountRepository;
import com.candidateupskill.backend.identity.security.JwtService;

@Service
public class AuthService {

	private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

	private final UserAccountRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final SecureRandom secureRandom;
	private final Duration refreshTokenTtl;

	public AuthService(
		UserAccountRepository userRepository,
		RefreshTokenRepository refreshTokenRepository,
		PasswordEncoder passwordEncoder,
		JwtService jwtService,
		@Value("${app.security.refresh-token-days}") long refreshTokenDays
	) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.secureRandom = new SecureRandom();
		this.refreshTokenTtl = Duration.ofDays(refreshTokenDays);
	}

	@Transactional
	public AuthSessionResponse signup(SignupRequest request) {
		String email = normalizeEmail(request.email());
		if (userRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
		}

		UserAccount user = new UserAccount(
			UUID.randomUUID(),
			email,
			passwordEncoder.encode(request.password()),
			request.name().trim(),
			UserStatus.ACTIVE
		);
		userRepository.save(user);
		return createSession(user);
	}

	@Transactional
	public AuthSessionResponse login(LoginRequest request) {
		UserAccount user = userRepository.findByEmail(normalizeEmail(request.email()))
			.orElseThrow(() -> invalidCredentials());

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw invalidCredentials();
		}

		return createSession(user);
	}

	@Transactional
	public AuthSessionResponse refresh(String rawRefreshToken) {
		RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hashToken(rawRefreshToken))
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

		if (!refreshToken.isUsable(Instant.now())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is expired or revoked");
		}

		refreshToken.revoke();
		return createSession(refreshToken.getUser());
	}

	@Transactional(readOnly = true)
	public UserProfileResponse currentUser(UUID userId) {
		return userRepository.findById(userId)
			.map(UserProfileResponse::from)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User no longer exists"));
	}

	private AuthSessionResponse createSession(UserAccount user) {
		String refreshToken = createRefreshToken();
		refreshTokenRepository.save(new RefreshToken(
			UUID.randomUUID(),
			user,
			hashToken(refreshToken),
			Instant.now().plus(refreshTokenTtl)
		));

		return new AuthSessionResponse(
			jwtService.createAccessToken(user),
			refreshToken,
			UserProfileResponse.from(user)
		);
	}

	private String createRefreshToken() {
		byte[] bytes = new byte[32];
		secureRandom.nextBytes(bytes);
		return BASE64_URL_ENCODER.encodeToString(bytes);
	}

	private String hashToken(String token) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return BASE64_URL_ENCODER.encodeToString(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception exception) {
			throw new IllegalStateException("Unable to hash refresh token", exception);
		}
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase();
	}

	private ResponseStatusException invalidCredentials() {
		return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
	}
}
