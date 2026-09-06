# ADR 004: Use Cucumber Feature Files for Functional Scenarios

## Status

Accepted

## Context

The feature specifications already contain user stories, acceptance criteria, and
stable `AC-*` and `FR-*` identifiers. Plain JUnit/MockMvc tests can execute the
behavior, but broad Java methods make the functional intent harder to review and
connect to the original scenarios.

The project also has Hurl as an independent RealWorld contract oracle. Cucumber
must therefore add readable project-owned scenarios without replacing Hurl or
becoming a second ungoverned product specification.

## Options Considered

1. Keep only JUnit/MockMvc tests and tags.
2. Replace the internal tests with Hurl scenarios.
3. Add Cucumber JVM feature files as a readable functional layer, backed by Spring
   Boot step definitions, while keeping JUnit for lower-level tests and Hurl for
   external conformance.

## Decision

Choose option 3.

Feature files live under `src/test/resources/features/`. Step definitions live in
`src/test/java/com/conduit/bdd/` and use MockMvc against the Spring test context.
Their scenarios carry the `AC-*` and `FR-*` tags from the feature manifest.

Cucumber is pinned to `7.20.1` because Spring Boot `3.5.5` manages JUnit Platform
`1.12.2`; newer Cucumber releases require JUnit Platform APIs not present in that
managed version. The Cucumber lane uses H2 for fast feedback. PostgreSQL behavior
continues to be verified by the separate Testcontainers lane and external Hurl
conformance remains independent.

The first feature file is a pilot. Future feature files are generated from the
structured `test-cases.yaml` artifact and reviewed as derived executable evidence.

## Consequences

### Positive

- Acceptance behavior is reviewable in business-readable Gherkin.
- Stable requirement and acceptance tags remain searchable in the test report.
- Existing JUnit, Testcontainers, and Hurl lanes retain their separate purposes.
- Step definitions can be reused across future feature files.

### Negative

- Cucumber adds a dependency and a second test vocabulary.
- Feature files and step definitions need maintenance when contracts evolve.
- Maven's JUnit Platform reporting currently reports the Cucumber engine as zero
  tests even though scenarios and steps execute; the Cucumber output remains the
  scenario-level execution evidence.

### Neutral

The specification remains the product source of truth. Feature files are derived
and executable acceptance evidence, not a replacement for `spec.md`.
