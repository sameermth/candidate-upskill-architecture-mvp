# Backend and Mobile Change Policy

Backend and mobile should evolve together through a contract-first workflow.

## Rule

Any backend change that affects mobile behavior must include the matching mobile impact in the same branch or in a linked follow-up issue.

## Required Steps

1. Update `docs/api/openapi.yaml`.
2. Update backend implementation.
3. Update mobile API client or service wrapper.
4. Update affected mobile screens or state handling.
5. Add or update tests for the changed behavior.

## Pull Request Checklist

- API contract updated when endpoint/request/response changed.
- Mobile impact reviewed.
- Backward compatibility considered.
- Auth, error, loading, and empty states handled in the app.

