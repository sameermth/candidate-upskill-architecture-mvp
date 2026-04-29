package com.candidateupskill.backend.resume.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.candidateupskill.backend.identity.security.AuthenticatedUser;
import com.candidateupskill.backend.platform.operation.api.OperationAcceptedResponse;
import com.candidateupskill.backend.resume.service.ResumeService;

@RestController
@RequestMapping("/resumes")
public class ResumeController {

	private final ResumeService resumeService;

	public ResumeController(ResumeService resumeService) {
		this.resumeService = resumeService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	public OperationAcceptedResponse upload(@RequestPart("file") MultipartFile file, Authentication authentication) {
		AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
		return resumeService.upload(user.id(), file);
	}
}
