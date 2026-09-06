# Quickstart: User Authentication and Current User Management

## Prerequisites

- A running Conduit API configured with an external JWT secret and database URL.
- Hurl installed and available on `PATH`.
- The API reachable at `http://localhost:3000`.

The implementation must fail at startup when required secrets or database
configuration are missing, without printing their values.

## Run the acceptance suite

From the repository root:

```bash
HOST=http://localhost:3000/api ./conformance/run-api-tests-hurl.sh
```

The authentication scenarios are sourced from
[`conformance/hurl/auth.hurl`](../../conformance/hurl/auth.hurl) and
[`conformance/hurl/errors_auth.hurl`](../../conformance/hurl/errors_auth.hurl).
They cover registration, login, current-user retrieval, account updates,
nullable fields, uniqueness conflicts, authentication failures, password length,
and token transport.

## Focused HTTP checks

1. Register a unique member with a password of at least 8 characters; expect
   `201` and a `user` response containing email, username, bio, image, and token.
2. Sign in with the registered credentials; expect `200` and a new `User`
   response without any password field.
3. Request `GET /api/user` with `Authorization: Token <jwt>`; expect `200`.
4. Request `GET /api/user` without the header or with `Bearer <jwt>`; expect
   `401`.
5. Update `PUT /api/user` with bio or image; expect `200`, then repeat `GET` to
   prove persistence using the same token.
6. Register or update with an existing email or username; expect `409` and a
   field-keyed `errors` response.
7. Register or update with an invalid field or a password shorter than 8
   characters; expect `422` and a field-keyed `errors` response.
8. Inspect every success and error response; confirm no password or hash is
   present and the content type is
   `application/json; charset=utf-8`.

## Design evidence

- [API contract](contracts/user-authentication.openapi.yaml)
- [Data model](data-model.md)
- [Feature specification](spec.md)
