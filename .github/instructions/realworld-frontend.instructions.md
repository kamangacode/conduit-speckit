---
applyTo: "**/*.tsx, **/*.css, docs/prd/specifications/frontend/**"
---

# RealWorld frontend educational terrain

This instruction is conditional on the future Conduit frontend terrain; it does not assume that a web application exists in this repository.

When frontend components are created, keep the markup, classes and invariants expected by the RealWorld spec. Do not introduce an in-house design system or rename contract classes without documented reason.

The frontend never speaks directly to the database: the data goes through the API. User-dependent states must follow the contract, including `following`, `favorited`, and the `Authorization: Token <jwt>` header.
