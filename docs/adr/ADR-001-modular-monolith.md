# ADR-001: Modular Monolith Architecture

- Status: Accepted
- Date: 2026-04-29
- Owners: Architecture + Backend

## Context
The MVP has fast-changing product requirements across resume intelligence, preparation roadmap, mock interviews, quick-fit, and job scoring. Team size and timeline favor rapid iteration and lower operational overhead.

## Decision
Build MVP as a modular monolith (Spring Boot), with strict internal module boundaries and event-driven async processing.

## Decision Drivers
1. Fast feature iteration
2. Low operational complexity
3. Transactional consistency across tightly coupled workflows
4. Easy local development and debugging

## Options Considered
1. Modular monolith
2. Microservices from day one

## Consequences
- Positive: Faster delivery, simpler deployment, easier refactoring early.
- Negative: Risk of module coupling if boundaries are not enforced.

## Implementation Notes
- Enforce module ownership for tables/APIs.
- Use outbox/events to prepare future service extraction.
