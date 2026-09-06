# Implementation Plan: User Authentication and Current User Management

**Branch**: `001-user-authentication` | **Date**: 2026-09-06 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/001-user-authentication/spec.md`

## Summary

Implement the four RealWorld Conduit user operations: registration, login,
current-user retrieval, and current-user update. The design uses a framework-free
user domain, application use cases, infrastructure adapters for persistence,
password hashing, and JWT signing/verification, plus HTTP adapters that preserve
the exact RealWorld envelopes, status codes, and headers. Every endpoint is
proved through real HTTP integration tests and the repository's Hurl conformance
suite.

## Technical Context

**Language/Version**: Java 25 LTS

**Primary Dependencies**: Spring Boot, Spring Data JPA/Hibernate, Maven, Argon2id
password hashing, and JJWT for JWT signing and verification

**Storage**: PostgreSQL target, accessed only through infrastructure adapters

**Testing**: MockMvc or WebTestClient integration tests; Hurl conformance suite;
plain Java domain tests

**Target Platform**: JVM web service

**Project Type**: REST web service

**Performance Goals**: Complete account and current-user operations within the
existing service's normal request budget; no feature-specific latency target is
specified by the PRD. Hurl acceptance must complete without contract timeouts.

**Constraints**: `Authorization: Token <jwt>`; JSON response content type must be
`application/json; charset=utf-8`; duplicate identity conflicts return 409; other
validation failures return 422; missing or invalid authentication returns 401;
passwords are Argon2id-hashed and never exposed; JWT secret and database URL are
external required configuration values.

**Scale/Scope**: Four endpoints and one User account aggregate. Public profiles,
following, articles, comments, favorites, logout, token revocation, and password
reset are out of scope.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **I. RealWorld Contract Supremacy**: PASS. The contract artifact references
  the PRD and executable Hurl/OpenAPI behavior, including `Token`, `User`, 409,
  422, 401, and content type rules.
- **II. Framework-Isolated Domain**: PASS. Domain types and rules are plain Java;
  Spring, JPA, hashing, JWT, and persistence mappings stay outside `domain/`.
- **III. Real HTTP Integration Tests**: PASS. Each of the four endpoints has an
  HTTP acceptance scenario, with Hurl as the external contract gate.
- **IV. Externalized Secrets and Fail-Fast Configuration**: PASS. JWT secret and
  database URL are required external settings; startup and error behavior are
  included in the quickstart and implementation tasks.
- **V. Hashed Passwords Only**: PASS. Argon2id is selected; response and error
  contracts explicitly exclude passwords and hashes.

## Project Structure

### Documentation (this feature)

```text
specs/001-user-authentication/
├── spec.md
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── user-authentication.openapi.yaml
└── checklists/
    └── requirements.md
```

### Source Code (repository root)

```text
src/
├── main/java/com/conduit/
│   ├── domain/user/
│   ├── application/user/
│   ├── infrastructure/persistence/
│   ├── infrastructure/security/
│   └── interface/http/
└── test/java/com/conduit/
    ├── domain/user/
    ├── application/user/
    └── interface/http/

conformance/
├── hurl/auth.hurl
├── hurl/errors_auth.hurl
└── openapi.yml
```

**Structure Decision**: Use the repository constitution's hexagonal package
boundaries. `domain/user` contains framework-free account concepts and rules;
`application/user` owns registration, login, current-user, and update use cases;
  `infrastructure/persistence` owns PostgreSQL repositories, JPA entities, and
  mappings;
`infrastructure/security` owns Argon2id and JWT ports/adapters; and
`interface/http` owns request mapping, response serialization, and HTTP status
mapping. Tests mirror these boundaries, with mandatory HTTP integration coverage
for every endpoint.

## Phase 0: Research

Research is consolidated in [research.md](research.md). It resolves the only
implementation-significant ambiguity: duplicate identity conflicts use `409`
according to the executable RealWorld contract, while ordinary validation uses
`422`. It also records the Argon2id choice, the 8-character password policy, and
the decision to preserve the current JWT through updates because the contract
has no revocation behavior.

## Phase 1: Design and Contracts

- [data-model.md](data-model.md) defines the private User aggregate, token
  boundary, invariants, response projection, and state transitions.
- [contracts/user-authentication.openapi.yaml](contracts/user-authentication.openapi.yaml)
  defines the four external endpoints, request envelopes, User response, error
  shape, status codes, security scheme, and content type.
- [quickstart.md](quickstart.md) defines runnable Hurl acceptance checks,
  configuration prerequisites, and non-disclosure verification.

## Constitution Check: Post-Design

- **Contract**: PASS. The design explicitly reconciles the official 409 conflict
  behavior with 422 validation behavior and preserves `Token`, `User`, and header
  invariants.
- **Domain isolation**: PASS. The data model has no framework annotations or
  persistence concerns; adapters are named in infrastructure only.
- **HTTP proof**: PASS. The quickstart maps each endpoint to a real HTTP check and
  points to the Hurl source of truth.
- **Secrets and fail-fast**: PASS. The quickstart requires external JWT/database
  settings and prohibits value disclosure; implementation must add startup tests.
- **Password safety**: PASS. Argon2id, minimum length, no composition restriction,
  and response/log/error non-disclosure are explicit in research and data model.
- **JWT dependency**: PASS. JJWT is selected and documented in
  [ADR 001](../../docs/adr/001-jjwt-for-jwt.md).

No constitution violations require complexity tracking.

## Complexity Tracking

No violations.
