<!--
Sync Impact Report
- Version change: 1.2.0 -> 1.3.0
- Modified principles: Development Workflow now includes evidence generation and
	the ordered Cucumber, PostgreSQL, Hurl, and Bruno gates.
- Added sections: RealWorld contract, framework isolation, HTTP integration
	testing, externalized secrets, password hashing, and workflow constraints.
- Removed sections: none.
- Follow-up TODOs: determine the original ratification date.
- Amendment: update the pedagogical implementation baseline from Java 21 to
	Java 25 LTS.
- Amendment: standardize Spring Data JPA/Hibernate as the persistence adapter
	for the Java terrain.
- Amendment: use `interfaces/rest/` for Java REST inbound adapters because
	`interface` is a Java keyword.
-->

# Conduit Constitution

## Core Principles

### I. RealWorld Contract Supremacy
The PRD at `docs/prd/PRD-conduit.md` and the RealWorld specification are the
source of truth. When an implementation preference conflicts with the contract,
the contract MUST win. HTTP authentication MUST use `Authorization: Token <jwt>`;
validation errors MUST return HTTP 422 with `{"errors":{"champ":["message"]}}`;
article list endpoints MUST omit `body`; unauthenticated `following` and
`favorited` MUST be `false`; and responses MUST use
`Content-Type: application/json; charset=utf-8`.

### II. Framework-Isolated Domain
Code under `domain/` MUST be plain Java and MUST NOT contain Spring or JPA
annotations, including `@Entity`, `@Service`, and `@Autowired`. Domain logic
MUST remain testable without a Spring context. Persistence and framework mapping
MUST live under `infrastructure/`.

### III. Real HTTP Integration Tests
Every endpoint MUST have an integration test that traverses the HTTP layer using
MockMvc, WebTestClient, or an equivalent real application boundary. Unit tests
of services or test-only wiring do not satisfy this requirement. Contract,
authentication, validation, serialization, and status-code behavior MUST be
verified at the HTTP boundary.

### IV. Externalized Secrets and Fail-Fast Configuration
JWT secrets and database URLs MUST come from external configuration and MUST NOT
be hard-coded. Application startup MUST fail when a required configuration value
is absent. Failure messages and logs MUST identify the missing setting without
printing its value.

### V. Hashed Passwords Only
Passwords MUST be hashed with Argon2id or BCrypt before persistence. The raw
`password` value MUST NOT appear in any response, log, exception, or validation
message. API DTOs and serializers MUST make accidental password exposure
impossible or fail a dedicated integration test.

## Additional Constraints

The implementation terrain is Java 25 LTS, Spring Boot, and Maven. Code MUST respect
the package boundaries `domain/`, `application/`, `infrastructure/`, and
`interfaces/`; REST inbound adapters MUST reside under `interfaces/rest/`.
The package name `interface/` MUST NOT be used because `interface` is a Java
keyword. The PRD and RealWorld specification MUST be linked from any
feature specification that changes an API contract. Any deliberate divergence
from those sources MUST be documented and covered by a contract or integration
test. Persistence MUST use Spring Data JPA/Hibernate behind application ports;
JPA entities and repositories MUST remain in `infrastructure/`, and Flyway MUST
remain the schema migration source of truth.

## Development Workflow

Changes MUST follow the repository's Spec Kit cycle:
`/speckit-constitution` -> `/speckit-specify` -> `/speckit-clarify` when needed
-> `/speckit-plan` -> `/speckit-tests` -> `/speckit-tasks` -> `/speckit-implement` ->
`/speckit-converge`. Reviews MUST verify contract invariants, domain/framework
separation, HTTP integration coverage, secret handling, and password non-
disclosure before a change is considered complete.

`/speckit-tests` MUST derive an `AC-*` case from every acceptance scenario and an executable
Cucumber scenario tagged with that `AC-*` and its `FR-*` references. Cucumber validates internal
business behavior. Hurl MUST remain the independent external RealWorld-contract gate and run only
after Cucumber; Bruno synchronization follows Hurl. H2 remains the quick feedback lane. When
Docker is available, PostgreSQL/Testcontainers and Flyway MUST run; a container startup failure
blocks completion. The final `traceability.md` MUST contain a matrix for each FR-* and AC-* with
its Cucumber, optional JUnit, optional Testcontainers, Hurl evidence, and execution status.

## Governance
This constitution supersedes conflicting local implementation preferences. Every
amendment MUST update the Sync Impact Report, explain its compatibility impact,
and update affected specifications, plans, tasks, and tests before
implementation. Versioning follows semantic versioning: MAJOR for removed or
redefined obligations, MINOR for new or materially expanded obligations, and
PATCH for clarifications that do not change obligations. Each review MUST record
evidence for the applicable gates; a missing test or unexplained contract
divergence blocks completion.

**Version**: 1.3.0 | **Ratified**: TODO(RATIFICATION_DATE): original adoption date is unknown | **Last Amended**: 2026-09-06
