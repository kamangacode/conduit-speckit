# ADR 003: Keep H2 and PostgreSQL as Separate Test Lanes

## Status

Accepted

## Context

The Java project needs fast feedback for unit and HTTP tests, while persistence and
migration behavior must also be verified against the production database engine,
PostgreSQL. H2 is already used by the test profile. Testcontainers provides a
throwaway PostgreSQL instance, but it requires a reachable Docker daemon.

A single fallback inside one test would hide PostgreSQL failures and could make a
passing H2 test look like PostgreSQL coverage.

## Options Considered

1. Use H2 for every test and skip PostgreSQL coverage.
2. Require Docker for every test and remove H2.
3. Keep two explicit lanes: H2 for fast tests, PostgreSQL/Testcontainers when Docker
   is available; fail when Docker is available but the PostgreSQL container or test
   fails.

## Decision

Choose option 3.

H2 remains the fast local lane for `@SpringBootTest` and `@DataJpaTest` tests using
the `test` profile. `UserRepositoryPostgresTest` is a separate integration lane
that runs a real PostgreSQL 16 container with Flyway enabled.

The PostgreSQL lane performs a `docker info` preflight. If no Docker CLI/daemon is
available, that lane is explicitly skipped and H2 remains available. If Docker is
available, the test starts Testcontainers without `disabledWithoutDocker`; any
container, migration, connection, or assertion failure fails the build.

Testcontainers modules are pinned to the published `1.21.4` release. The
repository does not rely on the developer's global `~/.testcontainers.properties`
for correctness.

## Consequences

### Positive

- Fast feedback remains available on machines without Docker.
- PostgreSQL behavior is tested when the environment supports it.
- A broken PostgreSQL container test cannot silently become a passing H2 test.
- The two kinds of evidence remain visible and independently reportable.

### Negative

- A local run without Docker does not prove PostgreSQL compatibility.
- CI must provide a working Docker service to make the PostgreSQL lane mandatory.
- The test suite has two persistence configurations to maintain.

### Neutral

The external Hurl suite remains a separate HTTP contract oracle and is not replaced
by either database lane.
