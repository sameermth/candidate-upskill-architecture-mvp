package com.candidateupskill.backend.platform.operation.api;

import java.util.UUID;

import com.candidateupskill.backend.platform.operation.domain.OperationStatus;

public record OperationAcceptedResponse(UUID operationId, OperationStatus status, String statusUrl) {
}
