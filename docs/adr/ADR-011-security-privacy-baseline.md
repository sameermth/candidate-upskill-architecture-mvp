# ADR-011: Security and Privacy Baseline for Sensitive Candidate Data

- Status: Accepted
- Date: 2026-04-29

## Context
Platform processes sensitive personal data (resume, salary, interview answers, mock transcripts).

## Decision
Implement encryption, strict authorization, data minimization for AI calls, retention rules, and deletion workflows from MVP.

## Decision Drivers
1. User trust
2. Risk reduction
3. Compliance readiness

## Options Considered
1. Strong MVP baseline controls
2. Minimal controls with later hardening

## Consequences
- Positive: Safer launch and better enterprise readiness.
- Negative: Additional engineering effort in early phases.

## Implementation Notes
- Signed short-lived object URLs.
- Redact direct identifiers before model calls when not required.
- Provide user-initiated delete flows.
