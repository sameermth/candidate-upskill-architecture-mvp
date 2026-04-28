package com.candidateupskill.backend.identity.security;

import java.util.UUID;

public record JwtClaims(UUID userId, String email) {
}
