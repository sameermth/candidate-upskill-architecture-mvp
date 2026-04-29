package com.candidateupskill.backend.platform.operation.domain;

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
@Table(name = "operations")
public class Operation {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserAccount user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 64)
	private OperationType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private OperationStatus status;

	@Column(length = 64)
	private String resourceType;

	private UUID resourceId;

	@Column(length = 80)
	private String errorCode;

	@Column(length = 500)
	private String errorMessage;

	@Column(nullable = false)
	private Instant createdAt;

	@Column(nullable = false)
	private Instant updatedAt;

	private Instant completedAt;

	protected Operation() {
	}

	public Operation(UUID id, UserAccount user, OperationType type, OperationStatus status, String resourceType, UUID resourceId) {
		this.id = id;
		this.user = user;
		this.type = type;
		this.status = status;
		this.resourceType = resourceType;
		this.resourceId = resourceId;
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

	public OperationType getType() {
		return type;
	}

	public OperationStatus getStatus() {
		return status;
	}

	public String getResourceType() {
		return resourceType;
	}

	public UUID getResourceId() {
		return resourceId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public Instant getCompletedAt() {
		return completedAt;
	}
}
