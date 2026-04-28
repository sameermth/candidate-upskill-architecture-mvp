# ADR-013: Authentication and Authorization Strategy

- Status: Accepted
- Date: 2026-04-29

## Context
The platform handles sensitive candidate data and must support secure session management, user-scoped access control, and future extensibility for social login and enterprise controls.

## Decision
Adopt JWT-based authentication with short-lived access tokens and refresh token rotation. Enforce strict resource-level authorization checks on every user-scoped endpoint.

## Decision Drivers
1. Security for sensitive PII
2. Stateless API scaling
3. Session compromise risk reduction
4. Future support for external identity providers

## Options Considered
1. JWT access + rotating refresh tokens
2. Long-lived JWT only
3. Server-side session store only

## Consequences
- Positive: Better security posture and horizontal scalability.
- Negative: Added implementation complexity for refresh token lifecycle and revocation handling.

## Implementation Notes
- Access token TTL: short (for example 15 minutes).
- Refresh token rotation on each refresh.
- Store refresh token hashes only.
- Support token revocation on logout/password reset/account compromise.
- Apply role model: USER, OPS_ADMIN, DATA_ADMIN.
- Add per-endpoint user ownership checks by default.
