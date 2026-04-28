# ADR-003: Redis for Cache/Queue/Transient Control

- Status: Accepted
- Date: 2026-04-29

## Context
MVP requires low-latency read caching, async job processing, rate limiting, and short-lived locks/state.

## Decision
Use Redis for caching, queue backing, rate limiting, and transient coordination.

## Decision Drivers
1. Async throughput
2. Reduced DB load
3. Fast transient state operations
4. Simpler MVP queue/caching operations

## Options Considered
1. Redis + PostgreSQL
2. PostgreSQL-only for everything

## Consequences
- Positive: Better p95 latency and worker performance.
- Negative: Additional operational dependency.

## Implementation Notes
- Define key TTL policies.
- Instrument queue lag and cache hit ratio.
