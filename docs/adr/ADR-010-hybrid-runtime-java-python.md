# ADR-010: Hybrid Runtime (Java Core + Python Specialists)

- Status: Accepted
- Date: 2026-04-29

## Context
Core platform needs transactional domain reliability; some NLP tasks benefit from Python ecosystem.

## Decision
Keep core backend in Spring Boot; add Python workers only for specialized NLP/AI tasks where clearly beneficial.

## Decision Drivers
1. Domain maintainability
2. AI experimentation flexibility
3. Controlled operational complexity

## Options Considered
1. Java core + Python specialists
2. All-in Java
3. All-in Python

## Consequences
- Positive: Best-fit tooling per workload.
- Negative: Cross-runtime contracts and ops overhead.

## Implementation Notes
- Python services return typed payloads only; Java persists domain truth.
- Use queue/API boundary and schema contracts.
