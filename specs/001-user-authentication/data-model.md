# Data Model: User Authentication and Current User Management

## User Account

The private account aggregate owns the credentials and current-user data.

| Field | Type | Required on creation | Update behavior | Exposure |
|---|---|---:|---|---|
| email | string | yes | optional; unique; null/empty rejected | returned in `User` |
| username | string | yes | optional; unique; null/empty rejected | returned in `User` |
| password | private credential | yes | optional; minimum 8 characters; hashed before storage | never returned |
| bio | nullable string | no | optional; `null` is valid | returned in `User` |
| image | nullable URL string | no | optional; `null` is valid; empty value normalizes to null | returned in `User` |
| passwordHash | private derived value | generated | replaced only when password changes | never returned |

### Invariants

- `email` and `username` are unique across accounts.
- `passwordHash` is the only persisted password representation.
- A password is at least 8 characters and the implementation accepts at least 64
  characters without composition requirements.
- `bio` and `image` may be null.
- Account identity used by authenticated operations comes from the verified JWT,
  never from a client-supplied user identifier.

## Authentication Token

A signed JWT identifies the current member and is returned as `user.token`.

- Transport scheme: `Authorization: Token <jwt>`.
- Secret and signing configuration are external and required at startup.
- The token is not persisted as a password or returned in logs.
- Token revocation and rotation are outside this feature; the current token stays
  usable through account updates until its normal expiry.

## User Response

Every successful in-scope response has this envelope:

```json
{
  "user": {
    "email": "member@example.invalid",
    "token": "jwt.token.placeholder",
    "username": "member",
    "bio": null,
    "image": null
  }
}
```

The response has exactly the contract fields shown above. It contains neither
`password` nor `passwordHash`.

## State Transitions

1. **Anonymous -> Registered**: valid username, email, and password create a
   user account and issue a token.
2. **Registered -> Authenticated**: valid credentials issue a token.
3. **Authenticated -> Updated**: accepted fields are changed for the identity
   represented by the verified token; the response returns the updated `User`.
4. **Authenticated -> Authenticated**: the current token remains usable after an
   update; no logout or revocation transition is part of this feature.
