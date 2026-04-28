package com.candidateupskill.backend.identity.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.candidateupskill.backend.identity.domain.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

	boolean existsByEmail(String email);

	Optional<UserAccount> findByEmail(String email);
}
