# Decisions Summary: ADR to Implementation Backlog Mapping

This document maps accepted ADRs to actionable implementation backlog items for the MVP.

## Priority Legend
- P0: Must complete before MVP beta launch
- P1: Complete during MVP build cycle
- P2: Post-beta hardening/optimization

## Owner Legend
- ARCH: Architecture
- BE: Backend Engineering
- FE: Frontend Engineering
- AI: AI/ML Engineering
- DEVOPS: Platform/DevOps
- QA: Quality Engineering
- SEC: Security/Compliance
- DATA: Data Engineering/Analytics

## ADR Backlog Map

| ADR | Decision | Key Backlog Items | Owner | Priority | Dependencies | Done Criteria |
|---|---|---|---|---|---|---|
| ADR-001 | Modular monolith | Define module boundaries and package conventions; enforce dependency rules; create module templates | ARCH, BE | P0 | None | Module boundaries documented and code scaffold created |
| ADR-002 | PostgreSQL primary store | Create initial schema and migration baseline; define indexes and FK strategy; set backup policy | BE, DEVOPS | P0 | ADR-001 | Migrations run cleanly in dev/staging; performance smoke checks pass |
| ADR-003 | Redis for cache/queue | Provision Redis per env; define key naming and TTL standards; implement cache invalidation rules | BE, DEVOPS | P0 | ADR-012 | Redis operational in all envs; cache hit ratio visible |
| ADR-004 | Async-first processing | Implement operation tracking API; convert long-running endpoints to async; worker process scaffolding | BE | P0 | ADR-001, ADR-003 | Async workflows return 202 + operationId; workers execute successfully |
| ADR-005 | Transactional outbox | Add outbox table and dispatcher; implement publish retry handling; add outbox monitoring metrics | BE, DEVOPS | P0 | ADR-002, ADR-004 | No lost-event gap in failure tests |
| ADR-006 | Central AI orchestration | Build orchestration module interface; centralize model calls; add task-type routing registry | BE, AI | P0 | ADR-001 | No feature module calls model provider directly |
| ADR-007 | Structured AI output contracts | Define JSON schemas per AI task; implement validation and repair/fallback flow; add rejection logging | BE, AI | P0 | ADR-006 | Schema validation coverage for all AI tasks |
| ADR-008 | Versioned deterministic scoring | Implement scoring policy tables and engine; store component explanations; add policy version linkage | BE, AI | P0 | ADR-002 | Same inputs produce same score under same policy version |
| ADR-009 | Aggregator-first ingestion | Build source adapters (Adzuna/Jooble); normalization pipeline; quality filters and dedupe logic | BE, DATA | P1 | ADR-004 | Ingestion run yields normalized active job set with dedupe metrics |
| ADR-010 | Hybrid runtime | Define Java-Python contract schemas; build optional Python worker harness; establish integration tests | ARCH, BE, AI | P1 | ADR-006, ADR-007 | Python worker can process contract-compliant tasks end-to-end |
| ADR-011 | Security/privacy baseline | Enforce authz checks; signed object URLs; encryption configs; sensitive log redaction | BE, SEC, DEVOPS | P0 | ADR-013, ADR-018 | Security checklist passed for MVP scope |
| ADR-012 | Delivery model/environments | Setup dev/staging/prod; CI/CD gates; migration checks; deployment rollback process | DEVOPS, BE, QA | P0 | None | Staging deploy is automated; prod deploy is controlled and reversible |
| ADR-013 | AuthN/AuthZ strategy | Implement JWT + refresh rotation; token revocation model; role and ownership middleware | BE, SEC | P0 | ADR-001 | Auth flows and ownership tests pass |
| ADR-014 | Queue technology choice | Implement queue topology and DLQs; define retry/backoff profiles per queue | BE, DEVOPS | P0 | ADR-003, ADR-004 | Queue lag and DLQ monitoring live; retry policy verified |
| ADR-015 | Model routing/fallback policy | Configure per-task model routes and budgets; fallback chain behavior; cost guardrail triggers | AI, BE | P1 | ADR-006, ADR-007 | Fallback and budget behavior observable in logs/metrics |
| ADR-016 | Scoring policy governance | Add scoring policy change process; rollout control and changelog standards; recompute job pipeline | ARCH, BE, AI | P1 | ADR-008 | Policy update can be staged and rolled back |
| ADR-017 | Retention/deletion policy | Implement retention jobs; user delete workflows; tombstone/audit records; queue cancel-on-delete | BE, SEC, DEVOPS | P1 | ADR-011 | Data deletion requests complete end-to-end with audit trace |
| ADR-018 | PII minimization/redaction | Build context minimization layer; PII redaction rules; redaction QA tests | AI, BE, SEC | P1 | ADR-006 | AI requests carry only approved context classes |
| ADR-019 | API idempotency | Implement idempotency key store; conflict handling; apply standard to critical endpoints | BE | P0 | ADR-004 | Duplicate retries do not create duplicate domain effects |
| ADR-020 | Observability + SLO policy | Instrument logs/metrics/traces; create dashboards; configure SLO alerts and runbooks | DEVOPS, BE, QA | P0 | ADR-012 | Golden signals and alerting active in staging/prod |

## MVP Execution Waves

### Wave 1 (Platform Foundation, P0)
- ADR-001, ADR-002, ADR-003, ADR-012, ADR-013

### Wave 2 (Async + AI Core, P0)
- ADR-004, ADR-005, ADR-006, ADR-007, ADR-019

### Wave 3 (Scoring + Security + Ops, P0/P1)
- ADR-008, ADR-011, ADR-020

### Wave 4 (Jobs + Governance + Hardening, P1)
- ADR-009, ADR-014, ADR-015, ADR-016, ADR-017, ADR-018

## Suggested RACI Snapshot

- Architecture governance: ARCH accountable
- Core backend delivery: BE accountable
- AI quality/routing/policies: AI accountable
- Platform reliability and CI/CD: DEVOPS accountable
- Security/privacy controls: SEC accountable
- Release verification: QA accountable

## Immediate Next Actions (7-day planning)

1. Finalize owner names against each ADR backlog item.
2. Create epics in tracker per execution wave.
3. Break each ADR backlog item into sprint-sized stories.
4. Add acceptance tests for P0 decisions before feature expansion.
5. Freeze MVP beta gate criteria tied to ADR-020 SLOs.
