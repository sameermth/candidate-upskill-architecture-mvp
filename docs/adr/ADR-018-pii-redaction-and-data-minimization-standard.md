# ADR-018: PII Redaction and Data Minimization Standard

- Status: Accepted
- Date: 2026-04-29

## Context
AI workflows require contextual data, but unnecessary exposure of direct identifiers increases privacy risk and compliance burden.

## Decision
Enforce a default redaction and minimization pipeline before external AI calls: include only required context slices and remove non-essential direct identifiers.

## Decision Drivers
1. Minimize sensitive data exposure
2. Reduce third-party processing risk
3. Align with privacy-by-design
4. Improve internal governance consistency

## Options Considered
1. Default-minimize and redact before AI calls
2. Send full raw artifacts for all tasks
3. Manual per-feature redaction

## Consequences
- Positive: Lower privacy risk and better governance controls.
- Negative: Requires robust context builders and task-specific data contracts.

## Implementation Notes
- Redact fields like name, email, phone, exact address unless strictly required.
- Build task-specific context schemas.
- Log redaction mode and context class metadata (not raw PII) for auditing.
- Periodically test prompt quality impact from minimization.
