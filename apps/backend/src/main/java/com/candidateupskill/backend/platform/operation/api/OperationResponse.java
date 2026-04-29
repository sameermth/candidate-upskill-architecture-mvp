package com.candidateupskill.backend.platform.operation.api;

import java.time.Instant;
import java.util.UUID;

import com.candidateupskill.backend.platform.operation.domain.Operation;
import com.candidateupskill.backend.platform.operation.domain.OperationStatus;
import com.candidateupskill.backend.platform.operation.domain.OperationType;

public record OperationResponse(
	UUID id,
	OperationType type,
	OperationStatus status,
	String resourceType,
	UUID resourceId,
	String errorCode,
	String errorMessage,
	Instant createdAt,
	Instant updatedAt,
	Instant completedAt
) {

	public static OperationResponse from(Operation operation) {
		return new OperationResponse(
			operation.getId(),
			operation.getType(),
			operation.getStatus(),
			operation.getResourceType(),
			operation.getResourceId(),
			operation.getErrorCode(),
			operation.getErrorMessage(),
			operation.getCreatedAt(),
			operation.getUpdatedAt(),
			operation.getCompletedAt()
		);
	}
}
