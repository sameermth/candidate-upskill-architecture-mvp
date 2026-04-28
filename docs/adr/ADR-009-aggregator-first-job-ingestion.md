# ADR-009: Aggregator-First Job Ingestion

- Status: Accepted
- Date: 2026-04-29

## Context
Need broad job coverage quickly without deep ATS/job-board integrations.

## Decision
Start with aggregator APIs (Adzuna/Jooble), normalize + dedupe internally, and redirect via original apply links.

## Decision Drivers
1. Speed to market
2. Coverage
3. Lower integration complexity

## Options Considered
1. Aggregator-first
2. Direct ATS/board integrations first

## Consequences
- Positive: Faster launch and broader initial inventory.
- Negative: More noise/duplicates; quality filters required.

## Implementation Notes
- Hybrid dedupe: deterministic keys + fuzzy similarity.
- Maintain quality score and freshness policy.
