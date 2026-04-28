# ADR-008: Versioned Deterministic Scoring Engine

- Status: Accepted
- Date: 2026-04-29

## Context
Readiness and job-fit scores drive user trust and decision-making; scores must be reproducible and explainable.

## Decision
Use deterministic formulas with policy versioning and component-level explanations.

## Decision Drivers
1. Reproducibility
2. Explainability
3. Controlled policy evolution

## Options Considered
1. Deterministic versioned scoring
2. Non-versioned or AI-only holistic scoring

## Consequences
- Positive: Strong trust/audit posture.
- Negative: Rescore workflows needed on policy updates.

## Implementation Notes
- Store policy_version_id and input snapshot hash with each score.
- Keep historical scores immutable.
