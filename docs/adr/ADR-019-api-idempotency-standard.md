# ADR-019: API Idempotency Standard for Mutating Operations

- Status: Accepted
- Date: 2026-04-29

## Context
Client retries, network failures, and async job triggers can cause duplicate writes and inconsistent state unless idempotency is enforced consistently.

## Decision
Require Idempotency-Key headers for critical mutating endpoints and enforce request-hash validation with deterministic replay behavior.

## Decision Drivers
1. Prevent duplicate operations
2. Improve client reliability under retry conditions
3. Protect async workflows from duplicate fan-out
4. Simplify incident debugging

## Options Considered
1. Endpoint-level idempotency standard
2. Best-effort dedupe in downstream workers only
3. No explicit idempotency controls

## Consequences
- Positive: Stronger correctness guarantees and safer retries.
- Negative: Requires key storage, TTL policies, and conflict handling paths.

## Implementation Notes
- Same key + same payload: return original response.
- Same key + different payload: return conflict.
- Apply to resume upload/reanalyze, roadmap generate/refresh, mock start/complete, quick-fit start/complete, and rescore triggers.
- Persist idempotency keys with scope and expiry.
