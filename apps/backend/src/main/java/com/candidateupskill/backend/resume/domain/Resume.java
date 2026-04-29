package com.candidateupskill.backend.resume.domain;

import java.time.Instant;
import java.util.UUID;

import com.candidateupskill.backend.identity.domain.UserAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "resumes")
public class Resume {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserAccount user;

	@Column(nullable = false, length = 255)
	private String fileName;

	@Column(nullable = false, length = 500)
	private String storageKey;

	@Column(nullable = false, length = 120)
	private String fileType;

	@Column(nullable = false)
	private long fileSizeBytes;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private ResumeParsedStatus parsedStatus;

	@Column(nullable = false)
	private boolean activeFlag;

	@Column(nullable = false)
	private Instant uploadedAt;

	@Column(nullable = false)
	private Instant createdAt;

	@Column(nullable = false)
	private Instant updatedAt;

	protected Resume() {
	}

	public Resume(UUID id, UserAccount user, String fileName, String storageKey, String fileType, long fileSizeBytes) {
		this.id = id;
		this.user = user;
		this.fileName = fileName;
		this.storageKey = storageKey;
		this.fileType = fileType;
		this.fileSizeBytes = fileSizeBytes;
		this.parsedStatus = ResumeParsedStatus.PENDING_PARSE;
		this.activeFlag = true;
		this.uploadedAt = Instant.now();
	}

	@PrePersist
	void prePersist() {
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		this.updatedAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}
}
