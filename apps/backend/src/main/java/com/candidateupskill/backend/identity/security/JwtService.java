package com.candidateupskill.backend.identity.security;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.candidateupskill.backend.identity.domain.UserAccount;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JwtService {

	private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

	private final ObjectMapper objectMapper;
	private final Clock clock;
	private final byte[] secret;
	private final Duration accessTokenTtl;

	public JwtService(
		ObjectMapper objectMapper,
		@Value("${app.security.jwt.secret}") String secret,
		@Value("${app.security.jwt.access-token-minutes}") long accessTokenMinutes
	) {
		this.objectMapper = objectMapper;
		this.clock = Clock.systemUTC();
		this.secret = secret.getBytes(StandardCharsets.UTF_8);
		this.accessTokenTtl = Duration.ofMinutes(accessTokenMinutes);
	}

	public String createAccessToken(UserAccount user) {
		Instant now = Instant.now(clock);
		Map<String, Object> header = new LinkedHashMap<>();
		header.put("alg", "HS256");
		header.put("typ", "JWT");

		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("sub", user.getId().toString());
		payload.put("email", user.getEmail());
		payload.put("iat", now.getEpochSecond());
		payload.put("exp", now.plus(accessTokenTtl).getEpochSecond());

		String unsignedToken = encodeJson(header) + "." + encodeJson(payload);
		return unsignedToken + "." + sign(unsignedToken);
	}

	public Optional<JwtClaims> parse(String token) {
		String[] parts = token.split("\\.");
		if (parts.length != 3) {
			return Optional.empty();
		}

		String unsignedToken = parts[0] + "." + parts[1];
		if (!sign(unsignedToken).equals(parts[2])) {
			return Optional.empty();
		}

		try {
			Map<String, Object> payload = objectMapper.readValue(BASE64_URL_DECODER.decode(parts[1]), new TypeReference<>() {
			});
			long exp = ((Number) payload.get("exp")).longValue();
			if (Instant.now(clock).isAfter(Instant.ofEpochSecond(exp))) {
				return Optional.empty();
			}

			return Optional.of(new JwtClaims(
				UUID.fromString((String) payload.get("sub")),
				(String) payload.get("email")
			));
		} catch (RuntimeException | java.io.IOException ignored) {
			return Optional.empty();
		}
	}

	private String encodeJson(Map<String, Object> value) {
		try {
			return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
		} catch (java.io.IOException exception) {
			throw new IllegalStateException("Unable to encode JWT JSON", exception);
		}
	}

	private String sign(String unsignedToken) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(secret, "HmacSHA256"));
			return BASE64_URL_ENCODER.encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception exception) {
			throw new IllegalStateException("Unable to sign JWT", exception);
		}
	}
}
