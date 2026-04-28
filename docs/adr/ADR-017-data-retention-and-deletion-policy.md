# ADR-017: Data Retention and Deletion Policy

- Status: Accepted
- Date: 2026-04-29

## Context
The platform stores sensitive user artifacts (resumes, interview answers, AI logs) and must balance product value, legal/privacy expectations, and storage/cost constraints.

## Decision
Adopt explicit retention windows per data class and implement user-triggered deletion workflows with end-to-end purge semantics across database, object storage, and async pipelines.

## Decision Drivers
1. Privacy and user trust
2. Compliance readiness
3. Cost and storage control
4. Operational clarity for lifecycle management

## Options Considered
1. Explicit retention + deletion workflows
2. Keep data indefinitely by default
3. Ad hoc manual cleanup

## Consequences
- Positive: Predictable data lifecycle, lower privacy risk, clearer compliance posture.
- Negative: Additional engineering effort for purge orchestration and auditability.

## Implementation Notes
- Define retention by class (for example AI logs 90-180 days, audit logs >=12 months).
- User delete actions must cascade to linked artifacts.
- Maintain minimal tombstones for audit of delete operations.
- Cancel or skip queued jobs that reference deleted artifacts.
