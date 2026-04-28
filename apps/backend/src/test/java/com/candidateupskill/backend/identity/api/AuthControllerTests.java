package com.candidateupskill.backend.identity.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void signsUpAndReturnsCurrentUserWithBearerToken() throws Exception {
		String email = "user-" + UUID.randomUUID() + "@example.com";

		MvcResult signup = postJson("/auth/signup", Map.of(
			"name", "Sameer Khan",
			"email", email,
			"password", "strong-password"
		))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.accessToken").isString())
			.andExpect(jsonPath("$.refreshToken").isString())
			.andExpect(jsonPath("$.user.email").value(email))
			.andReturn();

		String accessToken = responseJson(signup).get("accessToken").asText();

		mockMvc.perform(get("/auth/me")
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value(email))
			.andExpect(jsonPath("$.name").value("Sameer Khan"));
	}

	@Test
	void logsInWithRegisteredCredentials() throws Exception {
		String email = "login-" + UUID.randomUUID() + "@example.com";
		String password = "strong-password";
		createUser(email, password);

		postJson("/auth/login", Map.of(
			"email", email,
			"password", password
		))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").isString())
			.andExpect(jsonPath("$.refreshToken").isString())
			.andExpect(jsonPath("$.user.email").value(email));
	}

	@Test
	void refreshesSessionAndRotatesRefreshToken() throws Exception {
		String email = "refresh-" + UUID.randomUUID() + "@example.com";
		JsonNode signup = createUser(email, "strong-password");

		postJson("/auth/refresh", Map.of("refreshToken", signup.get("refreshToken").asText()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").isString())
			.andExpect(jsonPath("$.refreshToken").isString())
			.andExpect(jsonPath("$.user.email").value(email));
	}

	@Test
	void rejectsInvalidLogin() throws Exception {
		postJson("/auth/login", Map.of(
			"email", "missing@example.com",
			"password", "wrong-password"
		))
			.andExpect(status().isUnauthorized());
	}

	private JsonNode createUser(String email, String password) throws Exception {
		return responseJson(postJson("/auth/signup", Map.of(
			"name", "Test User",
			"email", email,
			"password", password
		)).andReturn());
	}

	private ResultActions postJson(String path, Object body) throws Exception {
		return mockMvc.perform(post(path)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(body)));
	}

	private JsonNode responseJson(MvcResult result) throws Exception {
		return objectMapper.readTree(result.getResponse().getContentAsString());
	}
}
