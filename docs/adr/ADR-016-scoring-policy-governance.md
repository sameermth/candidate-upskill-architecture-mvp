# ADR-016: Scoring Policy Governance and Change Control

- Status: Accepted
- Date: 2026-04-29

## Context
Job-fit and readiness scores directly influence user decisions. Uncontrolled scoring changes can reduce trust, break comparability, and create debugging ambiguity.

## Decision
Govern scoring through versioned policies with formal review, controlled rollout, and immutable historical score records linked to policy versions.

## Decision Drivers
1. User trust and explainability
2. Historical reproducibility
3. Safe policy iteration
4. Controlled experimentation and rollback

## Options Considered
1. Versioned policy governance with rollout controls
2. In-place formula edits without versioning
3. Pure AI-derived non-deterministic scoring

## Consequences
- Positive: Clear auditability and stable user experience.
- Negative: Requires additional release process for policy updates.

## Implementation Notes
- Store policies in `scoring_policy_versions`.
- Attach `policy_version_id` and input snapshot hash to each score.
- Use staged rollout (internal -> small cohort -> full rollout).
- Require changelog entry and approval for every policy revision.
- Support recompute jobs when policy changes are promoted.
