# ADR-021: Mobile App Architecture

- Status: Accepted
- Date: 2026-04-29

## Context

The product should be available on Android and iOS. The MVP needs a fast mobile development path while preserving the option to produce native app bundles and add native capabilities later.

## Decision

Use React Native for the mobile app, with Expo as the preferred MVP toolchain unless a native requirement forces a bare React Native setup.

## Decision Drivers

1. Shared mobile codebase for Android and iOS
2. Faster MVP iteration
3. Access to native build outputs when needed
4. Strong ecosystem for navigation, forms, secure storage, notifications, and app updates

## Options Considered

1. React Native with Expo
2. Bare React Native from day one
3. Separate native Android and iOS apps
4. Web-only frontend first

## Consequences

- Positive: Faster cross-platform delivery and simpler early mobile operations.
- Negative: Some advanced native integrations may require Expo config plugins or a later prebuild/native workflow.

## Implementation Notes

- Keep app code under `apps/mobile`.
- Treat `docs/api/openapi.yaml` as the shared backend/mobile contract.
- Use typed API clients or service wrappers in the mobile app.
- Keep secure tokens in platform secure storage, not plain async storage.

