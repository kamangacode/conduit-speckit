# Functional Test Traceability

Feature: [User authentication](spec.md)

The manifest in [`test-cases.yaml`](test-cases.yaml) is derived from the acceptance scenarios
in `spec.md`. The Java tests are the generated/internal regression layer; Hurl is the independent
external contract layer. A requirement is not considered evidenced by a test name alone: the
test must execute and assert the behavior described by the scenario.

| Requirement | Acceptance cases | Internal test evidence | Feature file | External evidence | Status |
|---|---|---|---|---|---|
| FR-001 | AC-US1-001 | `UserAuthenticationHttpTest#registersLogsInReadsAndUpdatesCurrentUserWithoutPassword` | `user-authentication.feature:4` | `auth.hurl` | covered |
| FR-002 | AC-US2-001 | `UserAuthenticationHttpTest#registersLogsInReadsAndUpdatesCurrentUserWithoutPassword` | `auth.hurl` | covered |
| FR-003 | AC-US1-001, AC-US2-001, AC-US3-001 | `UserAuthenticationHttpTest` | `user-authentication.feature:4` | `auth.hurl` | covered |
| FR-004 | AC-US1-001, AC-US2-001, AC-US3-001 | `UserAuthenticationHttpTest` | `user-authentication.feature:4` | `auth.hurl` | covered |
| FR-005 | AC-US2-003 | `UserAuthenticationHttpTest#rejectsMissingOrBearerAuthentication` | `auth.hurl` | covered |
| FR-006 | AC-US3-001 | `UserAuthenticationHttpTest#registersLogsInReadsAndUpdatesCurrentUserWithoutPassword` | `auth.hurl` | covered |
| FR-007 | AC-US4-001, AC-US4-004 | `UserAuthenticationHttpTest#registersLogsInReadsAndUpdatesCurrentUserWithoutPassword` | `auth.hurl` | partial |
| FR-008 | AC-US1-002, AC-US4-002 | `UserAuthenticationHttpTest#rejectsDuplicateAndInvalidRequestsAtHttpBoundary` | `errors_auth.hurl` | partial |
| FR-009 | AC-US1-003, AC-US4-003 | `UserAuthenticationHttpTest#rejectsDuplicateAndInvalidRequestsAtHttpBoundary` | `errors_auth.hurl` | partial |
| FR-010 | AC-US1-002, AC-US4-002 | `UserAuthenticationHttpTest#rejectsDuplicateAndInvalidRequestsAtHttpBoundary` | `errors_auth.hurl` | partial |
| FR-011 | AC-US1-003, AC-US4-003 | `UserAuthenticationHttpTest#rejectsDuplicateAndInvalidRequestsAtHttpBoundary` | `errors_auth.hurl` | partial |
| FR-012 | AC-US3-002, AC-US4-006 | `UserAuthenticationHttpTest#rejectsMissingOrBearerAuthentication` | `user-authentication.feature:15` | `errors_auth.hurl` | partial |
| FR-013 | AC-US1-004, AC-US3-003, AC-US4-005 | `UserAuthenticationHttpTest` | `auth.hurl` | partial |
| FR-014 | AC-US2-002, AC-US4-005 | `UserAuthenticationHttpTest` | `errors_auth.hurl` | partial |
| FR-015 | AC-US4-001 | `UserAuthenticationHttpTest` | `auth.hurl` | partial |
| FR-016 | out of scope by feature boundary | N/A | N/A | scoped out |

## Gaps to close in the generated-test pilot

- Split the current broad HTTP test into one generated test method per acceptance case.
- Add explicit assertions for `bio`, `image`, exact response fields, and content type on every
  relevant success and error response.
- Add an integration fixture for PostgreSQL/Testcontainers before claiming persistence coverage.
- Add a validator that rejects an `implemented` requirement without an executed evidence link.