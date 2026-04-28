# ADR-014: Queue Technology Choice for MVP

- Status: Accepted
- Date: 2026-04-29

## Context
The system requires asynchronous processing for resume parsing, roadmap generation, scoring, job ingestion, and report generation. Team needs low operational overhead and rapid implementation.

## Decision
Use Redis-backed queues for MVP worker orchestration, with a design that allows migration to a dedicated broker later if scale or reliability requirements increase.

## Decision Drivers
1. Fast MVP setup and developer productivity
2. Low infrastructure complexity
3. Adequate throughput for expected MVP load
4. Compatibility with current stack and operations

## Options Considered
1. Redis-backed queue system
2. RabbitMQ from day one
3. Kafka from day one

## Consequences
- Positive: Quick delivery and simpler operations early.
- Negative: Advanced broker semantics are more limited compared with Kafka/RabbitMQ.

## Implementation Notes
- Use per-domain queues and dedicated DLQs.
- Enforce idempotent job execution.
- Track queue lag, retry counts, and DLQ growth.
- Revisit broker decision at defined thresholds (for example sustained lag/SLA misses).
