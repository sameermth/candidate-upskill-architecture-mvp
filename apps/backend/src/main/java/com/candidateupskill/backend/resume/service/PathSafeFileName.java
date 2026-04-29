package com.candidateupskill.backend.resume.service;

final class PathSafeFileName {

	private PathSafeFileName() {
	}

	static String clean(String fileName) {
		String normalized = fileName.replace("\\", "/");
		return normalized
			.substring(normalized.lastIndexOf('/') + 1)
			.replaceAll("[\\r\\n\\t]", " ")
			.trim();
	}
}
