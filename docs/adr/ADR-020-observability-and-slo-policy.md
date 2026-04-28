# ADR-020: Observability and SLO Policy

- Status: Accepted
- Date: 2026-04-29

## Context
The product depends on async workflows and AI services with variable latency/cost. Reliable operations require shared telemetry standards and explicit service level objectives.

## Decision
Adopt unified observability standards (logs, metrics, traces, error tracking) with feature-level SLOs and alert policies across API, worker, ingestion, and AI layers.

## Decision Drivers
1. Fast incident detection and recovery
2. Performance and reliability governance
3. AI cost/performance transparency
4. Consistent cross-team operational language

## Options Considered
1. Full baseline observability + SLO policy
2. Logs-only monitoring
3. Ad hoc dashboards per team

## Consequences
- Positive: Better production reliability and faster debugging.
- Negative: Initial instrumentation and dashboard effort required.

## Implementation Notes
- Propagate requestId/traceId across API -> queue -> worker -> AI calls.
- Define SLOs for API availability/latency and async completion p95.
- Alert on queue lag, DLQ growth, AI fallback spikes, and 5xx errors.
- Track tokens/cost by feature and model route.
