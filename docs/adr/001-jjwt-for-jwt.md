## Status

Accepted

## Context

The authentication feature needs a Java library to sign and verify JWTs while
preserving Conduit's required `Authorization: Token <jwt>` transport scheme.
The implementation must keep JWT handling outside the framework-free domain.

## Options Considered

- JJWT, with signing and verification behind an application port.
- Spring Security resource-server JWT support.
- Hand-written JWT parsing and signing.

## Decision

Use JJWT for JWT signing and verification. Keep the dependency in the security
infrastructure adapter and expose only application-level token operations to
use cases. The HTTP adapter remains responsible for the literal `Token` prefix.

## Consequences

### Positive

- The choice is explicit and reproducible before implementation.
- JWT cryptography remains behind a replaceable infrastructure boundary.
- The custom RealWorld header scheme stays directly covered by HTTP tests.

### Negative

- The project carries a dedicated JWT dependency and must track its security
  updates.
- JJWT configuration and key handling require focused integration tests.

### Neutral

- Token rotation and revocation remain outside this feature's scope.