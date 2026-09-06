# Feature Specification: User Authentication and Current User Management

**Feature Branch**: `001-user-authentication`

**Created**: 2026-09-06

**Status**: Draft

**Input**: User description: "Périmètre : authentification et gestion de l'utilisateur courant de Conduit. Référence fonctionnelle : docs/prd/PRD-conduit.md sections 7.1, 8, 9, 10, et les règles R-8 (unicité email/username) et R-9 (mot de passe jamais renvoyé, stocké hashé). Un visiteur peut créer un compte et se connecter. Un membre authentifié peut consulter et modifier son compte. Les réponses respectent le format User de la section 8. Hors périmètre : profils publics, suivi, articles, commentaires."

## Clarifications

### Session 2026-09-06

- Q: Quelle longueur minimale faut-il imposer aux mots de passe ? -> A: Minimum
  de 8 caractères.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Create an account (Priority: P1)

As an anonymous visitor, I want to register with an email address, username, and
password so that I can become a Conduit member.

**Why this priority**: Account creation is the entry point for all authenticated
member capabilities.

**Independent Test**: Submit a valid registration request and verify that one
member account is created and a complete `User` response is returned without the
password.

**Acceptance Scenarios**:

1. **Given** no existing member uses the email or username, **When** the visitor
   submits all required registration fields, **Then** the account is created and
   the response contains `user.email`, `user.username`, `user.bio`, `user.image`,
   and `user.token`.
2. **Given** an existing member uses the email or username, **When** the visitor
   submits a registration request with that value, **Then** the request is
  rejected with status 409 and an `errors` object identifying the conflicting
   field.
3. **Given** a registration request is missing or invalid in a required field,
   **When** the visitor submits it, **Then** the request is rejected with status
   422 and field-specific error arrays.
4. **Given** a registration succeeds, **When** the response is inspected,
   **Then** no password or password hash is present in the response.

---

### User Story 2 - Sign in (Priority: P1)

As an existing member, I want to sign in with my email and password so that I
can access authenticated Conduit capabilities.

**Why this priority**: Sign-in establishes the authenticated identity used by all
current-user operations.

**Independent Test**: Register or provision a member, submit valid credentials,
and verify that the returned `User` contains a usable token and the member's
public account fields.

**Acceptance Scenarios**:

1. **Given** a member exists and the submitted credentials are correct, **When**
   the member signs in, **Then** the response is a `User` object containing
   email, username, bio, image, and token.
2. **Given** the email or password is incorrect or missing, **When** the visitor
   attempts to sign in, **Then** the request is rejected without revealing which
   credential failed and without returning a password.
3. **Given** a sign-in succeeds, **When** the member uses the returned token in
   an authenticated request, **Then** the token is accepted only with the
   `Authorization: Token <jwt>` scheme.

---

### User Story 3 - View the current account (Priority: P2)

As an authenticated member, I want to retrieve my current account so that I can
confirm my identity and account information.

**Why this priority**: Members need a reliable current-user representation after
sign-in and when restoring an authenticated session.

**Independent Test**: Authenticate as a member, request the current account, and
verify that the response identifies that member and contains the complete `User`
shape without a password.

**Acceptance Scenarios**:

1. **Given** a valid authentication token for a member, **When** the member
   requests the current account, **Then** the response contains that member's
   email, username, bio, image, and token in the `user` object.
2. **Given** no token or an invalid token, **When** a visitor requests the current
   account, **Then** the request is rejected with status 401.
3. **Given** a valid current-account response, **When** its fields are inspected,
   **Then** the response contains no password or password hash.

---

### User Story 4 - Update the current account (Priority: P2)

As an authenticated member, I want to update my email, username, password, bio,
or image so that my account information stays current.

**Why this priority**: Account maintenance is required for members to control
identity and profile data within this feature's scope.

**Independent Test**: Authenticate as a member, update one or more accepted
fields, and verify that the returned `User` and a subsequent current-account
request reflect the changes.

**Acceptance Scenarios**:

1. **Given** a valid token and an update containing one or more accepted fields,
   **When** the member updates the current account, **Then** the changed values
   are persisted and the response returns the updated `User` object.
2. **Given** a valid token and an update that changes email or username to a value
   used by another member, **When** the member submits the update, **Then** the
  request is rejected with status 409 and the conflicting field is identified.
3. **Given** a valid token and an update with an invalid password or invalid
   required value, **When** the member submits the update, **Then** the request
   is rejected with status 422 and field-specific error arrays.
4. **Given** a valid token and an update that sets nullable `bio` or `image` to
   null, **When** the member submits the update, **Then** the fields are stored as
   null and represented as null in the returned `User`.
5. **Given** a valid token, **When** the member updates the password, **Then** a
   later sign-in accepts the new password and no response exposes either the old
   or new password.
6. **Given** no token or an invalid token, **When** a visitor attempts to update
   an account, **Then** the request is rejected with status 401.

### Edge Cases

- Registration and account updates treat email and username comparisons as
  unique according to the product contract; conflicts return status 409 and are
  not silent overwrites.
- Empty or missing required registration and sign-in fields return status 422
  using `{"errors":{"field":["message"]}}`.
- Passwords shorter than 8 characters return status 422 during registration or
  account update.
- An invalid, malformed, or expired token cannot be used to read or update the
  current account and returns status 401.
- A request containing unsupported account fields does not change unrelated
  account data.
- Passwords and password hashes are absent from every success response and from
  validation or authentication error messages.
- All responses in this feature use `Content-Type: application/json; charset=utf-8`.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST allow an anonymous visitor to create an account
  using `username`, `email`, and `password`.
- **FR-002**: The system MUST allow an anonymous visitor to sign in using
  `email` and `password`.
- **FR-003**: The system MUST return successful authentication and current-user
  responses wrapped as `{ "user": { ... } }`.
- **FR-004**: Each successful `User` response MUST contain `email`, `token`,
  `username`, `bio`, and `image` with the values defined by the section 8
  contract.
- **FR-005**: The system MUST accept authenticated requests only when the
  `Authorization` header uses the `Token <jwt>` scheme.
- **FR-006**: The system MUST allow an authenticated member to retrieve only
  their current account.
- **FR-007**: The system MUST allow an authenticated member to update only
  `email`, `username`, `password`, `bio`, and `image` for their current account.
- **FR-008**: The system MUST enforce uniqueness of `email` and `username` on
  both account creation and account updates.
- **FR-009**: The system MUST require passwords to contain at least 8 characters
  during account creation and password updates.
- **FR-010**: The system MUST return HTTP 409 and an `errors` object identifying
  the conflicting field when email or username uniqueness is violated.
- **FR-011**: The system MUST return HTTP 422 and an `errors` object mapping field
  names to message arrays for other validation failures.
- **FR-012**: The system MUST return HTTP 401 when a current-account operation
  requires authentication and the request has no valid token.
- **FR-013**: The system MUST hash passwords before storing them and MUST never
  return a password or password hash.
- **FR-014**: The system MUST NOT include password values in authentication,
  validation, or account-management error messages.
- **FR-015**: The system MUST return `Content-Type: application/json; charset=utf-8`
  for every response in this feature.
- **FR-016**: The system MUST keep public profiles, following, articles,
  comments, and article favorites outside this feature's behavior and acceptance
  scope.

### Key Entities

- **User**: The authenticated member account represented in responses by email,
  username, bio, image, and token. The password is a private credential and is
  never part of the response representation.
- **Authentication token**: A signed credential returned in a successful `User`
  response and presented by a member to access current-account operations.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of the four in-scope operations (registration, sign-in,
  current-account retrieval, and current-account update) return the documented
  success or failure contract for their defined scenarios.
- **SC-002**: A new visitor can complete account creation and sign-in in two
  minutes or less when using valid input and a normal network connection.
- **SC-003**: 100% of successful `User` responses contain exactly the six
  contract fields `email`, `token`, `username`, `bio`, `image`, and the enclosing
  `user` object, with no password field.
- **SC-004**: 100% of duplicate email and duplicate username attempts are rejected
  with status 409 and identify the conflicting field.
- **SC-005**: 100% of unauthenticated current-account reads and updates are rejected
  with status 401, without exposing account data.
- **SC-006**: In acceptance testing, 0 password values or password hashes appear in
  successful responses, error responses, or observable account data.

## Assumptions

- The API base path is `/api`, and the four in-scope operations use the paths and
  methods defined in PRD section 7.1: `POST /api/users/login`, `POST /api/users`,
  `GET /api/user`, and `PUT /api/user`.
- JWT is the authentication mechanism defined by PRD section 9; token storage by
  a client is outside this feature's backend scope.
- `bio` and `image` are nullable, while `email`, `username`, and `password` are
  required for registration.
- Passwords must contain at least 8 characters.
- Update request fields are optional and only supplied fields are changed.
- Validation messages may vary by field and locale, but their response structure
  and status code are fixed by PRD section 10.
- The official RealWorld conformance suite is the acceptance reference when it
  covers one of these operations.
