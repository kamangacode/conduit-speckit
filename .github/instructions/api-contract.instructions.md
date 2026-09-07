---
applyTo: "**/interfaces/**/*.java, **/controller/**/*.java, specs/**/contracts/**, docs/prd/specifications/backend/**"
---

# API Conduit contracts

This instruction applies to contracts and the Conduit implementation target. Respect the RealWorld spec and contracts documented in `docs/prd/specifications/backend/`.

Authentication carries the JWT with `Authorization: Token <jwt>`, never `Bearer`. Validation errors use `422` and the form `{"errors":{"champ":["message"]}}`. Article lists do not return the `body` field when the contract excludes it. For an anonymous visitor, `following` and `favorited` are equivalent to `false`.

A controller validates and maps the input then delegates to the use case; it does not carry business logic. Any discrepancies with the contract must be documented and tested.
