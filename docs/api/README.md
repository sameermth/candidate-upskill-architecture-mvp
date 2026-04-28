# API Contracts

This directory holds API contracts shared by backend and mobile.

## Contract Flow

1. Update `openapi.yaml` for any backend endpoint change.
2. Implement or update backend behavior.
3. Regenerate or update the mobile API client.
4. Update affected mobile screens and tests.

The API contract is the handshake between `apps/backend` and `apps/mobile`.

