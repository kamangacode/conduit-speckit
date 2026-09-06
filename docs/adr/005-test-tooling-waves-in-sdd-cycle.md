# ADR 005: Add test tooling waves to the SDD cycle

## Status

Accepted

## Context

Conduit features require evidence at multiple levels: business scenarios, domain and
application rules, PostgreSQL persistence, and the independent RealWorld contract.
The existing Spec Kit cycle did not reserve a distinct stage for deriving and
validating test evidence before task generation. Consequently, generated acceptance
cases, Cucumber scenarios, JUnit tests, Hurl evidence, and traceability could drift
or be scheduled in an unsafe order.

The project retains H2 for fast feedback. PostgreSQL through Testcontainers is the
integration lane when Docker is available; a container startup failure in that state
must fail validation. Hurl remains the independent external contract oracle. Bruno
is derived from Hurl and can only be checked after Hurl succeeds.

## Options Considered

1. Keep test derivation inside `/speckit-tasks`.
2. Replace Hurl with Cucumber scenarios.
3. Add `/speckit-tests` between planning and task generation, while retaining Hurl
   as a separate contract gate.

## Decision

Adopt this cycle for new and resumed features:

```text
/speckit-specify -> /speckit-clarify -> /speckit-plan -> /speckit-tests ->
/speckit-tasks -> /speckit-implement -> /speckit-converge
```

`/speckit-tests` derives one `AC-*` case from each acceptance scenario and creates
tagged Cucumber scenarios using the corresponding `AC-*` and `FR-*` identifiers.
It reports ambiguities instead of inventing behavior. `/speckit-tasks` consumes
these artifacts and schedules their generation before implementation, then runs
Cucumber, PostgreSQL/Testcontainers, Hurl, Bruno synchronization, and traceability
in that order after implementation.

Each feature's `traceability.md` records a final matrix for every FR and AC, with
the Cucumber test, optional JUnit and Testcontainers evidence, Hurl evidence, and
execution status. JaCoCo starts as an informative measurement and becomes blocking
only after calibration. Other durability tooling is adopted only after an observed
need justifies it.

## Consequences

### Positive

- Business behavior and external contract evidence remain distinct and traceable.
- Tasks encode the execution order needed to keep generated Cucumber evidence ahead
  of Hurl validation.
- New sessions can resume the same feature workflow from versioned artifacts.

### Negative

- Features have an additional artifact-generation stage before task planning.
- Traceability maintenance is mandatory before declaring a feature complete.

### Neutral

- Existing H2 tests continue to provide fast feedback.
- Existing features are migrated when their next planning or convergence pass runs.