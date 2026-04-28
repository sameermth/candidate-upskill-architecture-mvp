# ADR-007: Strict Structured AI Output Contracts

- Status: Accepted
- Date: 2026-04-29

## Context
Free-form LLM outputs are fragile for downstream scoring and product logic.

## Decision
Require strict JSON schema validation + business-rule validation before domain persistence.

## Decision Drivers
1. Determinism
2. Safety
3. Explainability and reuse

## Options Considered
1. Strict schema contracts
2. Free-text parsing

## Consequences
- Positive: Stable downstream behavior.
- Negative: Extra retry/repair flows on invalid output.

## Implementation Notes
- One repair attempt, then fallback model, then failure path.
- Never persist invalid AI output as source truth.
