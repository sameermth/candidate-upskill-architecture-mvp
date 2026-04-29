package com.candidateupskill.backend.platform.operation.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.candidateupskill.backend.platform.operation.api.OperationResponse;
import com.candidateupskill.backend.platform.operation.repository.OperationRepository;

@Service
public class OperationService {

	private final OperationRepository operationRepository;

	public OperationService(OperationRepository operationRepository) {
		this.operationRepository = operationRepository;
	}

	@Transactional(readOnly = true)
	public OperationResponse getOperation(UUID operationId, UUID userId) {
		return operationRepository.findByIdAndUser_Id(operationId, userId)
			.map(OperationResponse::from)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operation not found"));
	}
}
