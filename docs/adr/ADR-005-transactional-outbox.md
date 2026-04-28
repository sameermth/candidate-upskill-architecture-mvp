# ADR-005: Transactional Outbox for Reliable Events

- Status: Accepted
- Date: 2026-04-29

## Context
Risk exists that domain writes succeed while event publication fails.

## Decision
Adopt transactional outbox pattern for reliable event emission.

## Decision Drivers
1. Exactly-once effect at domain level
2. Crash-safe event publication
3. Traceability for retries/failures

## Options Considered
1. Outbox pattern
2. Direct publish after DB commit

## Consequences
- Positive: Prevents lost events.
- Negative: Requires outbox dispatcher and monitoring.

## Implementation Notes
- Persist outbox row in same DB transaction as domain update.
- Dispatcher publishes and marks as delivered.
