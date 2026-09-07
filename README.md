# Conduit Spec Kit

Conduit Spec Kit is a spec-driven development lab
(SDD, *Specification-Driven Development*) around the API contract
[RealWorld](https://github.com/gothinkster/realworld). The educational runtime is
a Java 25 API, Spring Boot, Spring Data JPA/Hibernate and PostgreSQL.

The repository shows how to connect product intent, architectural decisions,
executable tasks, code and validation proofs.

## Prerequisites

- Java 25 LTS. Check with `java -version`.
- Maven Wrapper included. Check with `./mvnw --version`.
- Docker Desktop with Docker Compose v2. Check with `docker compose version`.
- [Hurl](https://hurl.dev/) for HTTP compliance.
- [Bruno](https://www.usebruno.com/) or Bun if Bruno tests are used.

The project uses the Maven Wrapper to make commands reproducible.

## Start PostgreSQL

The [`docker-compose.yml`](docker-compose.yml) file starts PostgreSQL 16 with:

- the base `conduit-speckit`;
- user `conduit`;
- the local port `5432` by default;
- the volume named `conduit-speckit-postgres-data`;
- a `pg_isready` healthcheck.

The password is not stored in Git. Prepare a local environment from
of the model:

```bash
cp .env.example .env
```

Replace the placeholder values ​​of `.env`, then start the database:

```bash
docker compose up -d postgres
docker compose ps
```

View logs or stop the service:

```bash
docker compose logs -f postgres
docker compose down
```

`docker compose down` keeps the volume. To also delete local data,
explicitly use `docker compose down -v`.

## Start API

The `DATABASE_*` and `JWT_SECRET` variables are read by Spring Boot. Docker Compose
automatically reads `.env`, but Maven does not inject it into the Java process:

```bash
set -a
source .env
set +a
./mvnw spring-boot:run
```

The API is then available on `http://localhost:8080`. Flyway migration creates the
PostgreSQL schema at startup and Hibernate then checks the schema with
`ddl-auto=validate`.

To use another base or another port, define `DATABASE_URL`,
`DATABASE_USERNAME`, `DATABASE_PASSWORD` and optionally `POSTGRES_PORT` in
the environment. Never commit a real secret.

## Test the code

Automated tests use H2 and do not require PostgreSQL:

```bash
./mvnw clean test
```

This suite notably covers authentication use cases,
HTTP controllers, token security and the JPA persistence adapter.

For a reproducible start with PostgreSQL:

```bash
set -a
source .env
set +a
docker compose up -d postgres
./mvnw spring-boot:run
```

In another terminal, check that the service is ready with `docker compose ps`.

## Test RealWorld Compliance

The reference scenarios are in [`conformance/hurl/`](conformance/hurl/).
Hurl is the source of truth for HTTP testing. The Bruno collection is generated at
from these scenarios and is used for interactive or alternative executions.

The API started by default on port `8080` must be targeted explicitly:

```bash
cd conformance
HOST=http://localhost:8080 ./run-api-tests-hurl.sh
```

For Bruno:

```bash
HOST=http://localhost:8080 ./run-api-tests-bruno.sh
```

Scripts can also receive targeted folders or files. For example :

```bash
HOST=http://localhost:8080 ./run-api-tests-hurl.sh hurl/auth.hurl
```

Detailed documentation of the collection can be found in
[`conformance/README.md`](conformance/README.md). The scenarios cover the
articles, authentication, comments, favorites, feed,
pagination, profiles, tags and authorization/validation errors.

For feature articles, reproducible gates use contracts
article-scoped `articles.hurl`, `pagination.hurl`, `tags.hurl` and
`articles-errors-scoped.hurl`, then the corresponding Bruno collections.
The feed, favorites and comments scenarios belong to the following iterations.

## Important contracts

- Authentication uses `Authorization: Token <jwt>`, never `Bearer`.
- Validation errors use HTTP `422` and the form
  `{"errors":{"champ":["message"]}}`.
- Article lists do not return the `body` field.
- For an anonymous visitor, `following` and `favorited` are equal to `false`.
- Passwords and password hashes are never exposed in
  HTTP responses.
- `JWT_SECRET`, `DATABASE_PASSWORD` and other secrets must remain in
  the local environment or in the deployment secrets manager.

These invariants are detailed in the [PRD](docs/prd/PRD-conduit.md) and the
feature contracts in [`specs/001-user-authentication/contracts/`](specs/001-user-authentication/contracts/).

## Spec Kit and SDD principles

The filing follows the following sequence:

```text
constitution -> specify -> clarify -> plan -> tests -> tasks -> implement -> converge
```

### 1. Constitution

The constitution defines the sustainable principles of the project: hexagonal architecture,
RealWorld contracts, security, testability, traceability and labor agreements.
It serves as a safeguard for future specifications and implementations.

### 2. Specify

The `/speckit-specify` command transforms a business request into a specification
observable: personas, scenarios, functional requirements and success criteria.
The specification describes the expected behavior, not the technical solution.

### 3. Clarify

The `/speckit-clarify` command asks targeted questions when the request is
ambiguous. The answers are encoded in the specification so that a reader without
history can understand the choices made.

### 4. Outline

The `/speckit-plan` command translates the specification into technical design:
architecture, data model, contracts, interfaces, dependencies and strategy
test. Structuring decisions are documented in the appropriate ADRs.

### 5. Testing

The `/speckit-tests` command derives each acceptance scenario in case `AC-*`, in scenario
Cucumber tagged with `AC-*` and `FR-*`, and initializes the traceability matrix. She points out the
ambiguities without inventing behavior. Cucumber proves internal business scenarios; Howl
remains the independent external contract oracle.

### 6. Tasks

The `/speckit-tasks` command produces ordered, traceable tasks. Every task
relates to a requirement, decision or expected proof. The tasks remain
in `specs/<feature>/tasks.md` and their state represents the actual progress.

### 7. Implement

The `/speckit-implement` command executes the tasks according to the plan. The code
is organized in hexagonal layers: `domain`, `application`, `infrastructure` and
`interfaces/rest` for inbound HTTP adapters. The domain and the cases
of use remain independent of Spring; JPA and Spring repositories
remain in the infrastructure.

The adapters are grouped by domain: `infrastructure/article/` and
`infrastructure/user/` for persistence and technical services, and
`interfaces/rest/article/`, `interfaces/rest/user/` and `interfaces/rest/shared/`
for HTTP adapters.

Lefthook orchestrates local checks: Spotless, Checkstyle and Gitleaks in
pre-commit, then `./mvnw verify -P integration` in pre-push.

### 8. Converge

The `/speckit-converge` command compares artifacts with code and evidence
actually available. It adds the missing tasks instead of declaring the
feature completed based solely on partial implementation.

## Artifacts and sources of truth

For the current authentication feature:

- [`spec.md`](specs/001-user-authentication/spec.md): intent and requirements;
- [`plan.md`](specs/001-user-authentication/plan.md): technical design;
- [`test-cases.yaml`](specs/001-user-authentication/test-cases.yaml): derived acceptance cases;
- [`traceability.md`](specs/001-user-authentication/traceability.md): evidence matrix;
- [`tasks.md`](specs/001-user-authentication/tasks.md): tasks and status;
- [`data-model.md`](specs/001-user-authentication/data-model.md): data model;
- [`contracts/`](specs/001-user-authentication/contracts/): API contracts;
- [`quickstart.md`](specs/001-user-authentication/quickstart.md): startup path;
- [`conformance/`](conformance/): HTTP conformance proofs;
- [`.github/instructions/`](.github/instructions/): rules applicable to code and documents;
- [`.github/skills/`](.github/skills/): behavior of Spec Kit commands.

Active conventions must remain in versioned instructions, ADRs and
feature artifacts. The documentation explains the path, but does not replace
not these sources of truth.

## Spec Kit Controls

In GitHub Copilot, commands use the `-` separator configured in
[`.specify/integration.json`](.specify/integration.json):

```text
/speckit-constitution
/speckit-specify
/speckit-clarify
/speckit-plan
/speckit-tests
/speckit-tasks
/speckit-implement
/speckit-converge
```

For a new feature, follow the sequence constitution, specification,
clarification, plan, testing, tasks, implementation, then convergence. Before concluding,
run Cucumber, PostgreSQL/Testcontainers when Docker is available, Hurl, then
Bruno synchronization, and update the traceability matrix.

## Execution architecture

- **Domain**: business rules and objects independent of Spring.
- **Application**: Use cases and outbound ports.
- **Infrastructure**: Spring Data JPA, Hibernate, Flyway, PostgreSQL and adapters.
- **HTTP**: controllers, response mapping, error handling and JWT filter.
- **Tests**: H2 for the fast loop, PostgreSQL Compose and Hurl/Bruno for the
  validation of execution and contract.

Flyway migrations are the source of truth for PostgreSQL schema. JPA entities
must not become a competing mechanism for creating or modifying the
plan.

## Quick troubleshooting

- **Compound refuses to start**: check that `POSTGRES_PASSWORD` is defined
  and that the chosen port is not already occupied. Use `POSTGRES_PORT=5433` if
  necessary, then adapt `DATABASE_URL`.
- **Spring cannot find the database**: check that `.env` has been loaded in the shell
  with `set -a; source .env; set +a` and `docker compose ps` indicates a service
  healthy.
- **The Hurl test does not find the API**: check that the API is running on `8080` and
  use `HOST=http://localhost:8080`. The scenarios themselves add the
  prefix `/api`.
- **A change of contract breaks a scenario**: first modify the
  Hurl specification and scenario concerned, then adapt the implementation and
  Bruno collection generated.

## Project Status

The depot is an SDD/Spec Kit educational field. Specification artifacts and
the existing tests indicate the scope implemented; tasks still open
remain the reference for measuring what is missing before declaring the feature complete.
