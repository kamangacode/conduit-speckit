# Research: User Authentication and Current User Management

## Decision 1: Contract authority and status mapping

- **Decision**: Use the PRD, the backend specification, and the official
  `conformance/` artifacts as the external contract. Return `409 Conflict` for
  duplicate email or username conflicts, and `422 Unprocessable Entity` for
  other validation failures.
- **Rationale**: The official OpenAPI and Hurl suite explicitly assert `409` for
  uniqueness conflicts, while the PRD's validation format applies to ordinary
  validation failures. This preserves R-8 without contradicting the executable
  conformance contract.
- **Alternatives considered**: Return 422 for every failure; rejected because it
  fails the repository's official duplicate-identity conformance cases.

## Decision 2: Password hashing

- **Decision**: Use Argon2id for password hashing. Keep the stored value private
  and expose no password or password hash through the `User` representation,
  logs, or error messages.
- **Rationale**: Argon2id is explicitly named in the project security and RGPD
  documentation and is permitted by the constitution. A dedicated password
  boundary keeps hashing out of the domain model while allowing HTTP tests to
  prove non-disclosure.
- **Alternatives considered**: BCrypt; permitted by the constitution, but less
  aligned with the repository's existing security guidance.

## Decision 3: Password validation policy

- **Decision**: Require at least 8 characters, accept at least 64 characters,
  and do not require character-class composition.
- **Rationale**: This is the policy exercised by
  `conformance/hurl/errors_auth.hurl` and follows the cited NIST-oriented test
  intent while avoiding unnecessary restrictions on passphrases.
- **Alternatives considered**: A 12-character minimum or composition rules;
  rejected because they are stricter than the executable contract.

## Decision 4: JWT transport and lifecycle

- **Decision**: Read authenticated identity only from a verified JWT presented as
  `Authorization: Token <jwt>`. Keep the current token valid through account
  updates within this feature and return a token in every successful `User`
  response.
- **Rationale**: `Token`, not `Bearer`, is required by the contract. The Hurl
  suite reuses the token after a current-user update, and the feature has no
  revocation store or logout contract. Token rotation/revocation is therefore
  outside this feature and must not be invented in the implementation.
- **Alternatives considered**: Rotate and revoke tokens after password changes;
  deferred because it requires a stateful revocation policy absent from the
  contract and would change the conformance behavior.

## Decision 5: Validation boundary

- **Decision**: Validate and map request envelopes at the HTTP boundary, then
  delegate business decisions to application use cases. Domain objects remain
  framework-independent; persistence and mapping remain infrastructure concerns.
- **Rationale**: This satisfies the constitution's framework isolation and API
  controller responsibilities while keeping the use cases independently
  testable.
- **Alternatives considered**: Put validation and password handling in the
  controller or persistence model; rejected because it couples contract and
  security behavior to adapters.

## Decision 6: Acceptance evidence

- **Decision**: Add HTTP integration coverage for all four endpoints and run the
  repository's Hurl authentication collection as the external contract gate.
- **Rationale**: The constitution rejects service-only proof. Hurl is the
  repository's executable source of truth and covers success, authentication,
  uniqueness, null handling, password policy, and non-disclosure cases.
- **Alternatives considered**: Unit tests only or generated Bruno tests only;
  rejected because they do not independently prove the real HTTP boundary or
  remain authoritative in this repository.

## Decision 7: JWT library

- **Decision**: Use JJWT for JWT signing and verification.
- **Rationale**: JJWT is already named in the repository's SDD examples, fits
  the Java implementation terrain, and keeps JWT handling behind the
  application `TokenService` port.
- **Alternatives considered**: Spring Security's higher-level resource-server
  support or hand-written JWT handling; rejected for this feature because the
  contract requires the custom `Token` header prefix and the port keeps that
  transport detail explicit and testable.
