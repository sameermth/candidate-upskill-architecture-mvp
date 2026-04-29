package com.candidateupskill.backend.resume.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.candidateupskill.backend.resume.domain.Resume;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {

	@Modifying
	@Query("update Resume resume set resume.activeFlag = false where resume.user.id = :userId and resume.activeFlag = true")
	void deactivateActiveForUser(UUID userId);
}
