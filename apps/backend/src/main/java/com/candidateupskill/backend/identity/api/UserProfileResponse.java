package com.candidateupskill.backend.identity.api;

import java.util.UUID;

import com.candidateupskill.backend.identity.domain.UserAccount;

public record UserProfileResponse(UUID id, String email, String name) {

	public static UserProfileResponse from(UserAccount user) {
		return new UserProfileResponse(user.getId(), user.getEmail(), user.getName());
	}
}
