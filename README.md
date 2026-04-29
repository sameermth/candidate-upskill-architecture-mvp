# Candidate Upskill Architecture MVP

Architecture, planning, and application workspace for the AI Interview Preparation and Job Readiness Platform.

## Repository Layout

- `apps/backend`: Spring Boot backend application workspace.
- `apps/mobile`: React Native mobile application workspace.
- `docs/adr`: Architecture Decision Records and implementation planning.
- `docs/api`: API contracts shared by backend and mobile.
- `docs/workflow`: Team workflows and change policies.

## First Vertical Slice

The current starter slice wires:

- backend health endpoint: `GET /api/v1/health`
- backend auth endpoints: `POST /auth/signup`, `POST /auth/login`, `POST /auth/refresh`, `GET /auth/me`
- backend resume upload and operation status: `POST /resumes`, `GET /operations/{operationId}`
- mobile API client: `apps/mobile/src/api/client.ts`
- mobile auth, secure token storage, and resume upload entry point: `apps/mobile/App.tsx`
- shared contract: `docs/api/openapi.yaml`

## Branching

- `main`: stable architecture and release baseline.
- `develop`: active development branch for backend, mobile, and contract changes.

All implementation changes should start from `develop`.
