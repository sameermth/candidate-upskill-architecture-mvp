# MVP Implementation Plan (3 Sprints)

This plan translates ADR decisions into a sprint-ready execution schedule.

## Planning Assumptions
- Sprint length: 2 weeks
- Team: 1 Architect, 3 Backend Engineers, 1 AI Engineer, 1 DevOps Engineer, 1 QA Engineer, 1 Frontend Engineer
- Scope target: backend + platform readiness for MVP beta

## Story ID Convention
- ARC-xxx: Architecture tasks
- BE-xxx: Backend tasks
- AI-xxx: AI/ML tasks
- OPS-xxx: DevOps/Platform tasks
- SEC-xxx: Security tasks
- QA-xxx: Quality tasks
- FE-xxx: Frontend integration tasks

## Sprint 1: Platform Foundation (Weeks 1-2)

### Goals
- Establish project skeleton, environments, auth baseline, schema foundation, and observability baseline.

### Stories

1. ARC-101: Define modular monolith boundaries and dependency rules
- ADR: 001
- Owner: ARCH
- Priority: P0
- Dependencies: None
- Acceptance: Module ownership doc and package structure merged.

2. BE-101: Create backend module scaffolding
- ADR: 001
- Owner: BE
- Priority: P0
- Dependencies: ARC-101
- Acceptance: All core modules compile and load with smoke test.

3. BE-102: Setup PostgreSQL schema v1 and migration pipeline
- ADR: 002
- Owner: BE
- Priority: P0
- Dependencies: ARC-101
- Acceptance: Migrations run in dev/staging with rollback-tested forward fix path.

4. OPS-101: Provision dev/staging environments with DB/Redis/object storage
- ADR: 012, 003
- Owner: DEVOPS
- Priority: P0
- Dependencies: None
- Acceptance: Environment check script passes for all services.

5. BE-103: Implement auth service (JWT + refresh rotation)
- ADR: 013
- Owner: BE
- Priority: P0
- Dependencies: BE-101, BE-102
- Acceptance: Signup/login/refresh/logout/me tested with token revocation coverage.

6. SEC-101: Define authz middleware and user ownership checks
- ADR: 013, 011
- Owner: SEC, BE
- Priority: P0
- Dependencies: BE-103
- Acceptance: Unauthorized cross-user access tests fail as expected.

7. OPS-102: CI/CD pipeline with PR gates and staged deploy
- ADR: 012
- Owner: DEVOPS
- Priority: P0
- Dependencies: None
- Acceptance: PR checks + auto deploy to staging + manual prod gate configured.

8. OPS-103: Baseline observability instrumentation (logs/metrics/traces)
- ADR: 020
- Owner: DEVOPS, BE
- Priority: P0
- Dependencies: BE-101
- Acceptance: Request traces and golden API metrics visible in dashboard.

9. QA-101: Test harness setup (unit/integration/contract smoke)
- ADR: 012, 020
- Owner: QA, BE
- Priority: P0
- Dependencies: BE-101
- Acceptance: CI test stages active with pass/fail gating.

### Sprint 1 Exit Criteria
- Auth and authorization baseline working.
- Schema + migrations stable.
- Dev/staging live with CI/CD.
- Foundational observability visible.

## Sprint 2: Async and AI Core (Weeks 3-4)

### Goals
- Implement async processing backbone, outbox reliability, AI orchestration, schema validation, and idempotency controls.

### Stories

1. BE-201: Implement operation tracking API and statuses
- ADR: 004
- Owner: BE
- Priority: P0
- Dependencies: BE-101
- Acceptance: Async endpoints return operationId with lifecycle transitions.

2. BE-202: Implement Redis queue topology and worker runner
- ADR: 014, 003, 004
- Owner: BE
- Priority: P0
- Dependencies: OPS-101
- Acceptance: Queue publish/consume and retry backoff verified.

3. BE-203: Add transactional outbox table + dispatcher
- ADR: 005
- Owner: BE
- Priority: P0
- Dependencies: BE-102, BE-202
- Acceptance: Failure simulation confirms no event loss.

4. BE-204: Implement idempotency key framework for critical endpoints
- ADR: 019
- Owner: BE
- Priority: P0
- Dependencies: BE-201
- Acceptance: Duplicate requests with same key do not duplicate domain state.

5. AI-201: Build AI orchestration module interface and task routing registry
- ADR: 006, 015
- Owner: AI, BE
- Priority: P0
- Dependencies: BE-101
- Acceptance: Feature modules invoke orchestrator only.

6. AI-202: Define JSON schemas and validation pipeline for core AI tasks
- ADR: 007
- Owner: AI, BE
- Priority: P0
- Dependencies: AI-201
- Acceptance: Schema validation enforced with rejection metrics.

7. AI-203: Implement fallback policy and per-task token budget controls
- ADR: 015
- Owner: AI
- Priority: P1
- Dependencies: AI-201
- Acceptance: Fallback chain exercised in staging failure tests.

8. BE-205: Implement resume upload -> async parse -> profile materialization flow
- ADR: 004, 006, 007
- Owner: BE, AI
- Priority: P0
- Dependencies: BE-201, BE-202, AI-202
- Acceptance: End-to-end resume processing works with operation tracking.

9. SEC-201: Implement PII minimization/redaction layer for AI requests
- ADR: 018, 011
- Owner: SEC, AI, BE
- Priority: P1
- Dependencies: AI-201
- Acceptance: Redaction tests pass and logs confirm minimization mode.

10. QA-201: Async reliability and idempotency integration suite
- ADR: 004, 005, 019
- Owner: QA
- Priority: P0
- Dependencies: BE-203, BE-204
- Acceptance: Retry, duplicate, and crash-recovery cases automated.

### Sprint 2 Exit Criteria
- Async backbone production-ready in staging.
- AI orchestration with validated outputs working.
- Idempotency and outbox reliability proven by tests.

## Sprint 3: Scoring, Jobs, Security Hardening (Weeks 5-6)

### Goals
- Deliver deterministic scoring engine, ingestion pipeline, retention/deletion, and SLO-ready operations.

### Stories

1. BE-301: Implement scoring policy/version tables and scoring engine v1
- ADR: 008, 016
- Owner: BE, AI
- Priority: P0
- Dependencies: BE-102, AI-202
- Acceptance: Score reproducibility tests pass by policy version.

2. BE-302: Add score component explanations and recommendation bucketing
- ADR: 008
- Owner: BE
- Priority: P0
- Dependencies: BE-301
- Acceptance: All job scores include component explanation payload.

3. BE-303: Build job ingestion adapters (Adzuna, Jooble)
- ADR: 009
- Owner: BE, DATA
- Priority: P1
- Dependencies: BE-202
- Acceptance: Scheduled ingestion populates normalized staging records.

4. BE-304: Implement normalization, quality scoring, and hybrid dedupe
- ADR: 009
- Owner: BE, DATA
- Priority: P1
- Dependencies: BE-303
- Acceptance: Duplicate rate and quality metrics available in dashboard.

5. BE-305: Implement recommendation scoring pipeline trigger and cache
- ADR: 008, 003, 009
- Owner: BE
- Priority: P1
- Dependencies: BE-301, BE-304
- Acceptance: Recommended jobs endpoint returns scored, explainable, filtered results.

6. SEC-301: Implement retention jobs and user deletion cascade workflows
- ADR: 017, 011
- Owner: SEC, BE
- Priority: P1
- Dependencies: BE-102, BE-202
- Acceptance: Deletion requests purge DB/object references with audit trail.

7. OPS-301: SLO dashboard and alert policy finalization
- ADR: 020
- Owner: DEVOPS, QA
- Priority: P0
- Dependencies: OPS-103, BE-202, AI-201
- Acceptance: Alerts configured for API errors, queue lag, DLQ growth, AI fallback spikes.

8. ARC-301: Scoring governance and policy rollout process doc
- ADR: 016
- Owner: ARCH, AI
- Priority: P1
- Dependencies: BE-301
- Acceptance: Version promotion and rollback process approved.

9. QA-301: End-to-end MVP beta gate test suite
- ADR: 011, 012, 020
- Owner: QA
- Priority: P0
- Dependencies: Sprint 3 core stories
- Acceptance: Beta gate checklist passes in staging.

### Sprint 3 Exit Criteria
- Explainable scoring available.
- Job ingestion/scoring loop operational.
- Security retention/deletion baselines active.
- SLO/alerts ready for beta.

## Critical Path
1. ARC-101 -> BE-101 -> BE-102 -> BE-201 -> BE-202 -> BE-203 -> AI-201 -> AI-202 -> BE-205 -> BE-301 -> QA-301

## Risk Register (Top 5)

1. AI schema failure rates too high
- Mitigation: strengthen templates, fallback, stricter context shaping
- Owner: AI

2. Queue lag under ingestion + scoring bursts
- Mitigation: worker autoscaling rules, priority queues, backpressure
- Owner: DEVOPS, BE

3. Score trust issues from weak explanations
- Mitigation: component-level rationale contract enforcement
- Owner: BE, AI

4. Data deletion not fully cascading
- Mitigation: delete orchestration tests and audit reconciliation job
- Owner: SEC, QA

5. Cost overrun from AI-heavy flows
- Mitigation: per-task budgets, caching, usage alarms
- Owner: AI, DEVOPS

## Beta Go/No-Go Checklist

1. AuthN/AuthZ penetration checks pass
2. Async reliability tests pass (retries, DLQ, idempotency)
3. AI schema validation >= target threshold
4. Score reproducibility validated against frozen policy version
5. Job ingestion quality and dedupe KPIs within accepted range
6. Retention and deletion workflows validated end-to-end
7. Observability dashboards + alerting active and tested

## Post-Plan Optional Extensions

1. Add Python specialist workers for advanced NLP extraction when needed.
2. Introduce pgvector for semantic retrieval after usage-driven thresholds.
3. Add controlled A/B testing framework for scoring and prompt policies.
