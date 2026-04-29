package com.candidateupskill.backend.platform.operation.api;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.candidateupskill.backend.identity.security.AuthenticatedUser;
import com.candidateupskill.backend.platform.operation.service.OperationService;

@RestController
@RequestMapping("/operations")
public class OperationController {

	private final OperationService operationService;

	public OperationController(OperationService operationService) {
		this.operationService = operationService;
	}

	@GetMapping("/{operationId}")
	public OperationResponse getOperation(@PathVariable UUID operationId, Authentication authentication) {
		AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
		return operationService.getOperation(operationId, user.id());
	}
}
