package com.candidateupskill.backend.resume.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ResumeControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void uploadsResumeAndCreatesPendingOperation() throws Exception {
		String accessToken = createAccessToken();
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"resume.txt",
			"text/plain",
			"Senior Java backend engineer with Spring Boot experience".getBytes()
		);

		MvcResult upload = mockMvc.perform(multipart("/resumes")
				.file(file)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isAccepted())
			.andExpect(jsonPath("$.operationId").isString())
			.andExpect(jsonPath("$.status").value("PENDING"))
			.andExpect(jsonPath("$.statusUrl").isString())
			.andReturn();

		String operationId = responseJson(upload).get("operationId").asText();

		mockMvc.perform(get("/operations/{operationId}", operationId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(operationId))
			.andExpect(jsonPath("$.type").value("RESUME_PARSE"))
			.andExpect(jsonPath("$.status").value("PENDING"))
			.andExpect(jsonPath("$.resourceType").value("RESUME"));
	}

	@Test
	void rejectsResumeUploadWithoutAuthentication() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"resume.txt",
			"text/plain",
			"Resume text".getBytes()
		);

		mockMvc.perform(multipart("/resumes").file(file))
			.andExpect(status().isForbidden());
	}

	private String createAccessToken() throws Exception {
		String email = "resume-" + UUID.randomUUID() + "@example.com";
		MvcResult signup = mockMvc.perform(post("/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(Map.of(
					"name", "Resume User",
					"email", email,
					"password", "strong-password"
				))))
			.andExpect(status().isCreated())
			.andReturn();

		return responseJson(signup).get("accessToken").asText();
	}

	private JsonNode responseJson(MvcResult result) throws Exception {
		return objectMapper.readTree(result.getResponse().getContentAsString());
	}
}
