package com.candidateupskill.backend.identity.api;

public record AuthSessionResponse(String accessToken, String refreshToken, UserProfileResponse user) {
}
