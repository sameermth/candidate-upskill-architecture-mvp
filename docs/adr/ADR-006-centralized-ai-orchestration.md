# ADR-006: Centralized AI Orchestration Layer

- Status: Accepted
- Date: 2026-04-29

## Context
Multiple features depend on AI; inconsistent direct model calls increase risk in quality, cost, and governance.

## Decision
All model interactions must go through a centralized AI orchestration module.

## Decision Drivers
1. Prompt/version governance
2. Schema validation
3. Cost and token controls
4. Unified fallback handling

## Options Considered
1. Central orchestration
2. Per-module direct LLM integration

## Consequences
- Positive: Consistent quality and auditability.
- Negative: Orchestrator becomes critical dependency.

## Implementation Notes
- Maintain task-type routing table.
- Persist request/output metadata with trace IDs.
