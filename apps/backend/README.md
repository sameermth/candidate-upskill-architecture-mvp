# Backend App

This directory is reserved for the Spring Boot modular monolith.

## Planned Responsibilities

- User identity and authorization
- Resume upload and profile extraction workflow
- Preparation roadmap and practice flows
- Mock interview and quick-fit workflows
- Job ingestion, scoring, and recommendations
- AI orchestration, auditing, and cost tracking

## Expected Stack

- Java 21
- Spring Boot
- PostgreSQL
- Redis
- Flyway or Liquibase
- OpenAPI contract generation

## Local Development

Run tests:

```bash
./gradlew test
```

Start the backend:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

Default local API base URL: `http://localhost:8080/api/v1`

The `local` and `test` profiles use an in-memory H2 database in PostgreSQL compatibility mode. The default runtime profile uses PostgreSQL connection settings from `application.properties`.

Uploaded resumes are stored under `app.storage.resume-dir`, which defaults to the system temp directory for local development.
