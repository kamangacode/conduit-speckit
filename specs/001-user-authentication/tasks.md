# Tasks: User Authentication and Current User Management

**Input**: Design documents from `/specs/001-user-authentication/`

**Prerequisites**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md), [data-model.md](data-model.md), [contracts/user-authentication.openapi.yaml](contracts/user-authentication.openapi.yaml), [quickstart.md](quickstart.md)

**Tests**: HTTP integration tests are mandatory because the project constitution requires every endpoint to be proven through the real HTTP boundary. Domain tests cover framework-free rules; Hurl remains the external contract gate.

**Organization**: Tasks are grouped by user story to enable independent implementation and validation.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Establish the Java 25 LTS Spring Boot service layout and reproducible test entry points.

- [X] T001 Create the Maven Java 25 project and dependency manifest in `pom.xml`
- [X] T002 [P] Create the hexagonal package directories under `src/main/java/com/conduit/`
- [X] T003 [P] Create mirrored test package directories under `src/test/java/com/conduit/`
- [X] T004 [P] Add test and local runtime configuration templates without secrets in `src/test/resources/application-test.yml` and `src/main/resources/application.yml`

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Implement shared domain, persistence, security, HTTP error, and test infrastructure required by every user story.

**Critical**: No user story work can begin until this phase is complete.

- [X] T005 Create the framework-free User aggregate and password invariants in `src/main/java/com/conduit/domain/user/User.java`
- [X] T006 [P] Define user persistence and lookup ports in `src/main/java/com/conduit/application/user/UserRepository.java`
- [X] T007 [P] Define password hashing and JWT ports in `src/main/java/com/conduit/application/user/PasswordHasher.java` and `src/main/java/com/conduit/application/user/TokenService.java`
- [X] T008 Create the PostgreSQL users schema migration with unique email and username constraints in `src/main/resources/db/migration/V1__create_users.sql`
- [X] T009 Implement the Spring Data JPA user repository, Hibernate entity, and persistence mapping in `src/main/java/com/conduit/infrastructure/persistence/SpringDataUserRepository.java`, `src/main/java/com/conduit/infrastructure/persistence/UserEntity.java`, and `src/main/java/com/conduit/infrastructure/persistence/UserRepositoryAdapter.java`
- [X] T010 Implement Argon2id hashing and password verification without logging raw credentials in `src/main/java/com/conduit/infrastructure/security/Argon2PasswordHasher.java`
- [X] T011 Implement JWT signing and verification from an external required secret in `src/main/java/com/conduit/infrastructure/security/JwtTokenService.java`
- [X] T012 Implement `Authorization: Token <jwt>` parsing and authenticated identity propagation in `src/main/java/com/conduit/interface/http/TokenAuthenticationFilter.java`
- [X] T013 Implement shared JSON error mapping for 401, 409, and 422 responses in `src/main/java/com/conduit/interface/http/ApiErrorHandler.java`

**Checkpoint**: The service can construct the User aggregate, persist it, hash and verify credentials, authenticate the exact Token scheme, and serialize contract errors without a Spring annotation in `domain/`.

## Phase 3: User Story 1 - Create an account (Priority: P1) 🎯 MVP

**Goal**: Allow an anonymous visitor to register a unique account and receive a complete password-free `User` response.

**Independent Test**: Submit valid and invalid registration requests through HTTP; verify 201 success, 409 uniqueness conflicts, 422 validation errors, the User envelope, and password non-disclosure.

### Tests for User Story 1

- [X] T014 [P] [US1] Add HTTP contract tests for `POST /api/users` in `src/test/java/com/conduit/UserAuthenticationHttpTest.java`
- [X] T015 [P] [US1] Add domain and application registration tests for required fields, minimum password length, and password non-disclosure in `src/test/java/com/conduit/application/user/RegisterUserUseCaseTest.java`

### Implementation for User Story 1

- [X] T016 [US1] Implement registration request/response DTOs and the User response mapper in `src/main/java/com/conduit/interface/http/UserResponseMapper.java`
- [X] T017 [US1] Implement the registration use case with Argon2id hashing and unique email/username conflict handling in `src/main/java/com/conduit/application/user/RegisterUserUseCase.java`
- [X] T018 [US1] Implement the anonymous registration route with HTTP 201, 409, 422, and `Content-Type` contract mapping in `src/main/java/com/conduit/interface/http/UserController.java`
- [ ] T019 [US1] Verify registration against the official Hurl scenarios in `conformance/hurl/auth.hurl` and `conformance/hurl/errors_auth.hurl`

**Checkpoint**: User Story 1 is independently functional and testable through the HTTP boundary.

## Phase 4: User Story 2 - Sign in (Priority: P1)

**Goal**: Allow an existing member to authenticate with email and password and receive a usable JWT in the User response.

**Independent Test**: Register a member, sign in with valid and invalid credentials through HTTP, and verify 200, 401, 422, Token transport, and credential non-disclosure.

### Tests for User Story 2

- [X] T020 [P] [US2] Add HTTP contract tests for `POST /api/users/login` in `src/test/java/com/conduit/UserAuthenticationHttpTest.java`
- [X] T021 [P] [US2] Add application login tests for password verification, invalid credentials, and token issuance in `src/test/java/com/conduit/application/user/LoginUserUseCaseTest.java`

### Implementation for User Story 2

- [X] T022 [US2] Implement the login use case with generic invalid-credential errors and JWT issuance in `src/main/java/com/conduit/application/user/LoginUserUseCase.java`
- [X] T023 [US2] Add the login route and response mapping to `src/main/java/com/conduit/interface/http/UserController.java`
- [ ] T024 [US2] Verify login and invalid-credential scenarios against `conformance/hurl/auth.hurl` and `conformance/hurl/errors_auth.hurl`

**Checkpoint**: User Stories 1 and 2 both work independently through HTTP; a member can create an account and authenticate.

## Phase 5: User Story 3 - View the current account (Priority: P2)

**Goal**: Allow an authenticated member to retrieve only the account identified by the verified JWT.

**Independent Test**: Call `GET /api/user` with a valid Token header, no header, a Bearer header, and an invalid token; verify 200 or 401 and password-free User serialization.

### Tests for User Story 3

- [X] T025 [P] [US3] Add HTTP contract tests for `GET /api/user` and the exact Token scheme in `src/test/java/com/conduit/UserAuthenticationHttpTest.java`
- [X] T026 [P] [US3] Add application tests proving the authenticated subject selects the current account in `src/test/java/com/conduit/application/user/GetCurrentUserUseCaseTest.java`

### Implementation for User Story 3

- [X] T027 [US3] Implement the current-user retrieval use case using the verified token subject in `src/main/java/com/conduit/application/user/GetCurrentUserUseCase.java`
- [X] T028 [US3] Add the authenticated `GET /api/user` route and 401 mapping to `src/main/java/com/conduit/interface/http/UserController.java`

**Checkpoint**: User Stories 1 through 3 are independently functional; authenticated reads cannot select another account by client-supplied identity.

## Phase 6: User Story 4 - Update the current account (Priority: P2)

**Goal**: Allow an authenticated member to update accepted account fields while preserving uniqueness, nullable-field, hashing, and response rules.

**Independent Test**: Authenticate, update each accepted field through HTTP, retrieve the account again, and verify persistence, 409/422 errors, password replacement, and non-disclosure.

### Tests for User Story 4

- [X] T029 [P] [US4] Add HTTP contract tests for `PUT /api/user` covering accepted fields, null values, unsupported fields, 401, 409, 422, and password non-disclosure in `src/test/java/com/conduit/UserAuthenticationHttpTest.java`
- [X] T030 [P] [US4] Add application tests for partial updates, uniqueness conflicts, password rehashing, and current-token continuity in `src/test/java/com/conduit/application/user/UpdateCurrentUserUseCaseTest.java`

### Implementation for User Story 4

- [X] T031 [US4] Implement update request validation for email, username, password, bio, image, and nullable semantics in `src/main/java/com/conduit/api/http/UserController.java`
- [X] T032 [US4] Implement the current-user update use case with partial changes, Argon2id replacement, and 409 uniqueness conflict handling in `src/main/java/com/conduit/application/user/UpdateCurrentUserUseCase.java`
- [X] T033 [US4] Add the authenticated `PUT /api/user` route and response/status mapping to `src/main/java/com/conduit/api/http/UserController.java`
- [X] T034 [US4] Ensure persistence mapping never exposes passwordHash through serializers or logs in `src/main/java/com/conduit/infrastructure/persistence/UserRecord.java` and `src/main/java/com/conduit/api/http/UserResponseMapper.java`
- [ ] T035 [US4] Verify update persistence, nullable normalization, password policy, and token continuity against `conformance/hurl/auth.hurl` and `conformance/hurl/errors_auth.hurl`

**Checkpoint**: All four in-scope endpoints are independently testable through the real HTTP boundary.

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Complete cross-cutting evidence, documentation, and security gates.

- [ ] T036 [P] Add fail-fast startup integration tests for missing JWT secret and database URL in `src/test/java/com/conduit/interface/http/RequiredConfigurationStartupTest.java`
- [ ] T037 [P] Add log and error redaction tests proving password values, passwordHash, and JWT secrets are absent from observable output in `src/test/java/com/conduit/security/SensitiveDataRedactionTest.java`
- [ ] T038 Run the complete Hurl API conformance suite and record the command and result in `specs/001-user-authentication/quickstart.md`
- [ ] T039 Update the API contract and data-model links if implementation paths or response behavior change in `specs/001-user-authentication/contracts/user-authentication.openapi.yaml` and `specs/001-user-authentication/data-model.md`
- [ ] T040 Validate the quickstart scenarios, all HTTP integration tests, and repository formatting in `specs/001-user-authentication/quickstart.md`
- [ ] T041 Record implementation evidence and requirement-to-test links in `specs/001-user-authentication/tasks.md`

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: T001-T004 have no feature dependencies and establish the Java/service layout.
- **Foundational (Phase 2)**: T005-T013 depend on Setup and block all user stories.
- **User Stories (Phases 3-6)**: Depend on T005-T013. US1 and US2 are P1; US3 and US4 are P2. US2 uses the account created by US1 in acceptance setup, while US3 and US4 use the shared authentication foundation.
- **Polish (Phase 7)**: Depends on the desired user stories being complete; T036-T037 can run in parallel with final documentation tasks after the foundation exists.

### User Story Dependencies

- **US1 (P1)**: Starts after Phase 2; MVP entry point. No dependency on another user story.
- **US2 (P1)**: Starts after Phase 2; its integration fixture may register a member through US1's endpoint, but login implementation is independently testable with a seeded repository.
- **US3 (P2)**: Starts after Phase 2; uses the token service and repository ports, not a client-supplied user id.
- **US4 (P2)**: Starts after Phase 2; shares the User aggregate and controller with earlier stories, so file-level work follows US1/US2 route registration.

### Requirement Traceability

| Requirement group | Tasks |
|---|---|
| FR-001, FR-003, FR-004, FR-008, FR-009, FR-010, FR-013, FR-014, FR-015 | T014-T019, T034 |
| FR-002, FR-003, FR-004, FR-005, FR-013, FR-014, FR-015 | T020-T024 |
| FR-005, FR-006, FR-003, FR-004, FR-012, FR-013, FR-015 | T025-T028 |
| FR-005, FR-007-FR-016 | T029-T035, T036-T040 |

### Success Criteria Traceability

| Success criterion | Evidence tasks |
|---|---|
| SC-001 | T014, T020, T025, T029, T038, T040 |
| SC-003 | T014, T020, T025, T029, T034 |
| SC-004 | T014, T029, T035 |
| SC-005 | T025, T029 |
| SC-006 | T015, T021, T029, T034, T037 |

## Parallel Opportunities

- **Setup**: T002, T003, and T004 can run in parallel after T001.
- **Foundation**: T006 and T007 can run in parallel; T008 can run in parallel with the port definitions. T010 and T011 can run in parallel after T007.
- **US1**: T014 and T015 can run in parallel before T016-T018.
- **US2**: T020 and T021 can run in parallel before T022-T023.
- **US3**: T025 and T026 can run in parallel before T027-T028.
- **US4**: T029 and T030 can run in parallel before T031-T034.
- **Polish**: T036, T037, and T039 can run in parallel once the relevant implementation exists.

## Parallel Example: User Story 1

```text
Task: T014 [P] [US1] HTTP contract tests in src/test/java/com/conduit/interface/http/UserRegistrationHttpTest.java
Task: T015 [P] [US1] Registration use-case tests in src/test/java/com/conduit/application/user/RegisterUserUseCaseTest.java
```

## Parallel Example: User Story 2

```text
Task: T020 [P] [US2] HTTP login tests in src/test/java/com/conduit/interface/http/UserLoginHttpTest.java
Task: T021 [P] [US2] Login use-case tests in src/test/java/com/conduit/application/user/LoginUserUseCaseTest.java
```

## Parallel Example: User Story 3

```text
Task: T025 [P] [US3] Current-user HTTP tests in src/test/java/com/conduit/interface/http/CurrentUserHttpTest.java
Task: T026 [P] [US3] Current-user use-case tests in src/test/java/com/conduit/application/user/GetCurrentUserUseCaseTest.java
```

## Parallel Example: User Story 4

```text
Task: T029 [P] [US4] Current-user update HTTP tests in src/test/java/com/conduit/interface/http/UpdateCurrentUserHttpTest.java
Task: T030 [P] [US4] Current-user update use-case tests in src/test/java/com/conduit/application/user/UpdateCurrentUserUseCaseTest.java
```

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 Setup.
2. Complete Phase 2 Foundational work.
3. Complete Phase 3 User Story 1.
4. Run `mvn test` for the focused registration tests and the registration Hurl scenarios.
5. Stop at the US1 checkpoint and verify account creation, uniqueness, validation, and password non-disclosure independently.

### Incremental Delivery

1. Add US2 login and validate it independently.
2. Add US3 current-user retrieval and validate Token-only authentication.
3. Add US4 current-user updates and validate persistence, uniqueness, nullable fields, hashing, and token continuity.
4. Run the full Hurl suite and the quickstart validation before declaring the feature complete.

### Parallel Team Strategy

1. Complete Setup and Foundational phases together.
2. After the foundation, assign US1/US2 to separate workers where shared controller changes are coordinated sequentially.
3. Assign US3 and US4 after the authentication ports and HTTP error mapping are stable.
4. Run Polish tasks as a final evidence and security pass.

## Notes

- Every task starts with a checkbox and sequential ID, includes `[P]` only for safe parallel work, and includes a story label for story-phase tasks.
- Every task names at least one concrete file path.
- HTTP integration tasks are mandatory; service-only tests do not satisfy the constitution.
- No task introduces secrets, real credentials, or raw password values into versioned files.

## Phase 8: Convergence

**Purpose**: Close the remaining evidence, runtime, security, and documentation gaps found after the initial implementation pass.

- [ ] T042 [US1] Run the registration Hurl scenarios against a configured Conduit runtime and record the 201, 409, 422, and password non-disclosure results per `US1/AC1`, `US1/AC2`, and `US1/AC4` (missing)
- [ ] T043 [US2] Run the login and invalid-credential Hurl scenarios against a configured Conduit runtime and record the 200, 401, 422, and Token-header results per `US2/AC1` and `US2/AC2` (missing)
- [ ] T044 [US4] Run the current-user update Hurl scenarios against PostgreSQL and verify persistence, nullable fields, password replacement, and token continuity per `US4/AC1`-`US4/AC5` (missing)
- [ ] T045 Add fail-fast startup integration tests for missing `JWT_SECRET` and `DATABASE_URL` in `src/test/java/com/conduit/api/http/RequiredConfigurationStartupTest.java` per Constitution IV (missing)
- [ ] T046 Add observability redaction tests proving password values, password hashes, JWT secrets, and database credentials never appear in logs or errors in `src/test/java/com/conduit/security/SensitiveDataRedactionTest.java` per FR-013 and FR-014 (missing)
- [ ] T047 Run the complete Hurl suite and update `specs/001-user-authentication/quickstart.md` with the actual command, environment prerequisites, and result per SC-001 and SC-006 (missing)
- [ ] T048 Record implementation evidence and final requirement-to-test links in `specs/001-user-authentication/tasks.md` and verify the contract/data-model references match the actual JPA package paths per SC-003 and SC-006 (missing)
