# ADR-004: Async-First Processing for Expensive Work

- Status: Accepted
- Date: 2026-04-29

## Context
Resume parsing, roadmap generation, ingestion, scoring, and report generation are long-running and variable-latency tasks.

## Decision
Process expensive workflows asynchronously via worker queues and return operation tracking IDs.

## Decision Drivers
1. Avoid API timeouts
2. Better UX for long-running operations
3. Retry and failure isolation

## Options Considered
1. Async workers with operation statuses
2. Synchronous request handling

## Consequences
- Positive: Resilience and responsiveness.
- Negative: Added complexity in job orchestration and status tracking.

## Implementation Notes
- Standard operation states: PENDING, RUNNING, SUCCEEDED, FAILED_*, CANCELLED.
- DLQ + retry with exponential backoff.
