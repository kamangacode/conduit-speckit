---
name: "speckit-tests"
description: "Derive executable functional-test cases and traceability evidence from a Spec Kit feature specification."
compatibility: "Requires a Spec Kit feature directory with spec.md; plan.md and contracts are used when present."
metadata:
  author: "Conduit project"
  source: "project workflow"
---

# Generate functional-test evidence

Generate the functional-test artifacts for the current feature after planning and before task
generation. The feature's `spec.md` remains the source of truth; generated files are derived
evidence and must never invent behavior that is absent from the specification or an explicit
contract.

## Inputs

Read, when present:

- `spec.md`: user stories, acceptance scenarios, FR-### requirements, and SC-### success criteria;
- `plan.md`: test stack, application boundary, persistence, and project structure;
- `contracts/`: externally observable request, response, status, and header rules;
- `quickstart.md`: runnable commands and environment prerequisites;
- project constitution and test instructions.

## Required outputs

Create or update these files under the feature directory:

```text
test-cases.yaml       # one stable AC-* case per acceptance scenario
traceability.md       # requirement -> case -> internal/external evidence
src/test/resources/features/*.feature  # readable executable scenarios when Cucumber is used
```

Each case must include:

- stable `AC-USx-yyy` identifier;
- story and `FR-###` references;
- test level (`domain`, `application`, `functional-http`, `security`, or
  `external-conformance`);
- concrete request or workflow;
- Given/When/Then behavior;
- expected status, headers, fields, and forbidden fields where applicable;
- expected internal test location and independent external oracle when one exists.
- feature-file location when the Java project uses Cucumber.

## Acceptance-case derivation

For every acceptance scenario, create exactly one stable `AC-USx-yyy` case. Derive exactly one
executable Cucumber scenario from that case and tag it with `@AC-USx-yyy` and every referenced
`@FR-###` tag. Preserve the scenario's Given/When/Then behavior and its observable status,
fields, headers, and forbidden fields.

When a scenario does not specify an observable contract detail, record it in `traceability.md`
under `## Ambiguities blocking test generation` with the source text and the unanswered question.
Do not infer a status, authorization rule, fixture, persistence semantic, or response field.
Do not emit a Cucumber scenario for a blocked case until `/speckit-clarify` resolves it.

## Generation rules

1. Preserve the acceptance scenario's observable behavior; do not translate it into an
   implementation detail.
2. Refuse to silently fill in missing statuses, fields, fixtures, authorization behavior, or
   persistence semantics. Emit a blocking ambiguity in `traceability.md` instead.
3. Generate negative assertions for security and privacy requirements, not only happy paths.
4. Prefer one case per acceptance scenario. Do not collapse distinct failure modes into a generic
   `returns an error` test.
5. Keep external conformance suites separate from generated internal tests. An internal test must
   not be counted as external evidence.
6. Every requirement marked implemented must have an executed evidence link. A test class name
   without a behavior assertion is not evidence.
7. Generated tests run before implementation and are expected to fail until the feature exists.

## Java test mapping

For the Java/Spring Boot project, generate or update tests at the boundary described by `plan.md`:

- framework-free rules: JUnit 5 domain tests;
- use cases: JUnit 5 application tests with ports replaced by focused fakes;
- HTTP behavior: Spring Boot Test with MockMvc or WebTestClient;
- real PostgreSQL behavior: Testcontainers and Flyway;
- architecture: ArchUnit;
- external contract: Hurl, kept outside generated Java tests.

the project's Cucumber runner.
Generated JUnit tests must carry `@Tag("AC-USx-yyy")` and the covered `@Tag("FR-###")` values.
Generated Cucumber scenarios must carry the same `@AC-*` and `@FR-*` tags and be executable by
the project's Cucumber runner. Keep Cucumber scenarios separate from Hurl: Cucumber proves the
project's business scenarios; Hurl proves the independent external RealWorld contract.
Use deterministic fixtures and never place real secrets or raw passwords in versioned files.

## Completion gate

Before reporting completion:

- validate YAML syntax and uniqueness of all case IDs;
- validate every referenced `FR-###` exists in `spec.md`;
- validate every acceptance scenario has one case;
- validate every implemented requirement has internal or external evidence;
- list gaps separately from covered cases;
- initialize the final traceability matrix with one row for every FR-* and AC-* identifier. Each
   row must name the Cucumber scenario, optional JUnit test, optional Testcontainers proof, Hurl
   proof, and an execution status of `not run`, `passed`, `failed`, `blocked`, or `not applicable`.
- do not edit `tasks.md`; `/speckit-tasks` consumes these generated artifacts.

## Local pilot command

This repository provides a dependency-free Ruby tool for the first pilot:

```bash
ruby scripts/functional-tests.rb validate specs/001-user-authentication
ruby scripts/functional-tests.rb generate specs/001-user-authentication target/generated-test-sources
```

The generated Java class contains one tagged, intentionally failing JUnit skeleton per case.
Move or replace each skeleton with a real assertion at the implementation boundary; never make a
skeleton green by asserting a constant or by adding `@Disabled` permanently.
