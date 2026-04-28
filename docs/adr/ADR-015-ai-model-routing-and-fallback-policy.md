# ADR-015: AI Model Routing and Fallback Policy

- Status: Accepted
- Date: 2026-04-29

## Context
Different AI tasks have different quality, latency, and cost requirements. A single-model strategy can be either too expensive or too low quality for critical flows.

## Decision
Implement task-based model routing with explicit fallback chains and per-task cost/token budgets managed by the AI orchestration module.

## Decision Drivers
1. Balance quality and cost
2. Improve resilience during provider/model failures
3. Ensure predictable behavior by task class
4. Support controlled experimentation

## Options Considered
1. Task-based routing + fallback chain
2. Single model for all tasks
3. Dynamic auto-routing without fixed policies

## Consequences
- Positive: Better cost-efficiency and reliability.
- Negative: Requires ongoing policy tuning and monitoring.

## Implementation Notes
- Define task classes: high-precision, medium-complexity, low-risk summarization.
- Configure fallback order: primary -> fallback-1 -> fallback-2 -> deterministic degraded response.
- Enforce strict schema validation regardless of model used.
- Log model, latency, tokens, cost, and fallback reason per request.
