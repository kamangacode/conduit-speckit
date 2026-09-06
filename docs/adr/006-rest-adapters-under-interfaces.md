# ADR 006: Place REST adapters under interfaces

## Status

Accepted

## Context

The Java application currently places controllers, HTTP filters, response mappers,
and HTTP configuration under `com.conduit.api.http`. The repository conventions
already distinguish `domain`, `application`, `infrastructure`, and an incoming
interface layer, while the `interface` directory is not a valid Java package name
because `interface` is a Java keyword.

The `api` name is ambiguous: it describes the exposed product surface, but these
classes are specifically Spring MVC adapters at the inbound hexagonal boundary.
The TypeScript implementation uses an `interface` directory for the same role.

## Options Considered

1. Keep the current `com.conduit.api.http` package.
2. Move the classes to `com.conduit.interfaces.rest` and keep `api` for future API metadata.
3. Move the classes to `com.conduit.api.interfaces.rest`.

## Decision

Move all current HTTP controllers, response mappers, filters, error handling, and
HTTP configuration to `com.conduit.interfaces.rest`.

Use `interfaces` rather than `interface` because `interface` is a Java keyword.
Use `rest` to make the transport protocol explicit. Organize by feature under
`interfaces.rest` later if the number of endpoints grows, for example
`interfaces.rest.article` and `interfaces.rest.user`.

The application layer remains independent of this package. An ArchUnit rule
ensures inbound adapters stay under `com.conduit.interfaces..` and that the
application/domain layers do not depend on them.

## Consequences

### Positive

- Package names express the hexagonal role of the HTTP adapters.
- The empty `interface` directory is replaced by a valid Java package.
- Future non-REST inbound adapters can coexist under `interfaces`.
- The boundary becomes enforceable with ArchUnit.

### Negative

- Java package declarations, imports, tests, and documentation must be migrated.
- Existing external references to `com.conduit.api.http` would need updates.

### Neutral

- HTTP routes and the RealWorld contract do not change.
- The `api` word remains available for documentation or generated contract artifacts.
