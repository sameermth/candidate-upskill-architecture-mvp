# ADR-012: Delivery Model and Environment Strategy

- Status: Accepted
- Date: 2026-04-29

## Context
Need safe iterative releases with observability, rollback capability, and schema evolution control.

## Decision
Use dev, staging, and prod environments; containerized deploys; migration-driven DB changes; SLO-gated releases.

## Decision Drivers
1. Deployment safety
2. Repeatability
3. Production confidence

## Options Considered
1. 3-env CI/CD with gates
2. Manual/single-environment release model

## Consequences
- Positive: Better reliability and release discipline.
- Negative: Upfront pipeline setup investment.

## Implementation Notes
- Mandatory PR checks + migration review.
- Post-deploy smoke + rollback criteria.
