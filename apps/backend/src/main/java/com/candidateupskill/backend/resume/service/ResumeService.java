package com.candidateupskill.backend.resume.service;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.candidateupskill.backend.identity.domain.UserAccount;
import com.candidateupskill.backend.identity.repository.UserAccountRepository;
import com.candidateupskill.backend.platform.operation.api.OperationAcceptedResponse;
import com.candidateupskill.backend.platform.operation.domain.Operation;
import com.candidateupskill.backend.platform.operation.domain.OperationStatus;
import com.candidateupskill.backend.platform.operation.domain.OperationType;
import com.candidateupskill.backend.platform.operation.repository.OperationRepository;
import com.candidateupskill.backend.resume.domain.Resume;
import com.candidateupskill.backend.resume.repository.ResumeRepository;

@Service
public class ResumeService {

	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
		"application/octet-stream",
		"application/pdf",
		"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
		"text/plain"
	);

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".docx", ".txt");

	private final UserAccountRepository userRepository;
	private final ResumeRepository resumeRepository;
	private final OperationRepository operationRepository;
	private final ResumeStorageService storageService;

	public ResumeService(
		UserAccountRepository userRepository,
		ResumeRepository resumeRepository,
		OperationRepository operationRepository,
		ResumeStorageService storageService
	) {
		this.userRepository = userRepository;
		this.resumeRepository = resumeRepository;
		this.operationRepository = operationRepository;
		this.storageService = storageService;
	}

	@Transactional
	public OperationAcceptedResponse upload(UUID userId, MultipartFile file) {
		validate(file);

		UserAccount user = userRepository.findById(userId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User no longer exists"));

		UUID resumeId = UUID.randomUUID();
		String storageKey = storageService.store(userId, resumeId, file);
		resumeRepository.deactivateActiveForUser(userId);
		Resume resume = resumeRepository.save(new Resume(
			resumeId,
			user,
			safeFileName(file),
			storageKey,
			contentType(file),
			file.getSize()
		));

		Operation operation = operationRepository.save(new Operation(
			UUID.randomUUID(),
			user,
			OperationType.RESUME_PARSE,
			OperationStatus.PENDING,
			"RESUME",
			resume.getId()
		));

		return new OperationAcceptedResponse(
			operation.getId(),
			operation.getStatus(),
			"/api/v1/operations/" + operation.getId()
		);
	}

	private void validate(MultipartFile file) {
		if (file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume file is required");
		}

		if (!ALLOWED_CONTENT_TYPES.contains(contentType(file))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF, DOCX, and plain text resumes are supported");
		}

		if (!ALLOWED_EXTENSIONS.contains(extension(file))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume file must end with .pdf, .docx, or .txt");
		}
	}

	private String contentType(MultipartFile file) {
		String contentType = file.getContentType();
		return contentType == null ? "application/octet-stream" : contentType.toLowerCase(Locale.ROOT);
	}

	private String safeFileName(MultipartFile file) {
		String original = file.getOriginalFilename();
		if (original == null || original.isBlank()) {
			return "resume";
		}
		return PathSafeFileName.clean(original);
	}

	private String extension(MultipartFile file) {
		String fileName = safeFileName(file);
		int dotIndex = fileName.lastIndexOf('.');
		return dotIndex >= 0 ? fileName.substring(dotIndex).toLowerCase(Locale.ROOT) : "";
	}
}
