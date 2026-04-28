# Mobile App

This directory is reserved for the React Native application.

## Recommended Direction

Use React Native with Expo for the MVP mobile app.

Expo keeps the first version fast to build while still supporting Android and iOS app bundles through the standard native build path when needed.

## Planned Responsibilities

- Onboarding and resume upload
- Candidate profile and preferences
- Daily preparation feed
- Practice questions and mock interview flow
- Quick Fit Assessment
- Job recommendations and score explanations

## Backend Contract Rule

The mobile app should consume backend APIs through generated or typed clients based on `docs/api/openapi.yaml`.

When backend endpoints change, update the API contract first, then update the mobile API client and affected screens.

