package com.candidateupskill.backend.resume.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeStorageService {

	private final Path resumeDirectory;

	public ResumeStorageService(@Value("${app.storage.resume-dir}") String resumeDirectory) {
		this.resumeDirectory = Path.of(resumeDirectory);
	}

	public String store(UUID userId, UUID resumeId, MultipartFile file) {
		String extension = extensionFor(file.getOriginalFilename());
		Path userDirectory = resumeDirectory.resolve(userId.toString());
		Path destination = userDirectory.resolve(resumeId + extension);

		try {
			Files.createDirectories(userDirectory);
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
			}
			return resumeDirectory.relativize(destination).toString();
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to store resume file", exception);
		}
	}

	private String extensionFor(String fileName) {
		if (fileName == null) {
			return "";
		}

		int dotIndex = fileName.lastIndexOf('.');
		return dotIndex >= 0 ? fileName.substring(dotIndex).toLowerCase() : "";
	}
}
