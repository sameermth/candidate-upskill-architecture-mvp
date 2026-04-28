# ADR-002: PostgreSQL as Primary Data Store

- Status: Accepted
- Date: 2026-04-29

## Context
Data is highly relational and trust-critical (scores, progress, preferences, recommendations) with need for strong consistency and analytics-friendly queries.

## Decision
Use PostgreSQL as system of record.

## Decision Drivers
1. ACID transactions
2. Relational integrity
3. Strong indexing/query capabilities
4. JSONB for semi-structured AI metadata

## Options Considered
1. PostgreSQL
2. NoSQL-first

## Consequences
- Positive: Reliable consistency and explainable analytics.
- Negative: Requires schema/index discipline as scale grows.

## Implementation Notes
- Use UUID keys, migration tooling, and audited schema changes.
- Consider pgvector later if semantic retrieval needs increase.
