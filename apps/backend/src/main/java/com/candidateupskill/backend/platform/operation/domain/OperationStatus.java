package com.candidateupskill.backend.platform.operation.domain;

public enum OperationStatus {
	PENDING,
	RUNNING,
	SUCCEEDED,
	FAILED_RETRYING,
	FAILED_PERMANENT,
	CANCELLED
}
