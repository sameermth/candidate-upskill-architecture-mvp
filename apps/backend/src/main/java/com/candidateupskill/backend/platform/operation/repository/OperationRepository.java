package com.candidateupskill.backend.platform.operation.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.candidateupskill.backend.platform.operation.domain.Operation;

public interface OperationRepository extends JpaRepository<Operation, UUID> {

	Optional<Operation> findByIdAndUser_Id(UUID id, UUID userId);
}
