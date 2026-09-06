---

description: "Task list for article publishing and discovery"
---

# Tasks: Article Publishing and Discovery

**Input**: `spec.md`, `plan.md`, `research.md`, `data-model.md`, `contracts/`, `test-cases.yaml`, `traceability.md`, and the existing Java/Spring codebase.

**Required evidence order**: validate AC cases -> verify Cucumber -> verify JUnit fixtures -> implement -> Cucumber -> PostgreSQL/Testcontainers -> Hurl -> Bruno -> traceability. Hurl is independent and is never replaced by Cucumber.

## Phase 1: Setup

- [X] T001 Validate the 13 AC cases with `ruby scripts/functional-tests.rb validate specs/002-articles` in `specs/002-articles/test-cases.yaml`
- [X] T002 Verify AC/FR tags and scenarios in `src/test/resources/features/articles.feature`
- [X] T003 Generate acceptance skeletons in `target/generated-test-sources/` with `ruby scripts/functional-tests.rb generate specs/002-articles target/generated-test-sources`
- [X] T004 Verify Java 25 and Maven Wrapper with `./mvnw --version` in `mvnw` and `.mvn/wrapper/maven-wrapper.properties`
- [X] T005 Configure H2 and PostgreSQL/Testcontainers lanes in `pom.xml` and `src/test/resources/application-test.yml`
- [X] T006 [P] Configure Spotless and Checkstyle in `pom.xml` and `config/checkstyle/checkstyle.xml`
- [X] T007 [P] Add Java 25 CI verification in `.github/workflows/verify.yml`
- [X] T008 [P] Add Lefthook orchestration in `lefthook.yml`
- [X] T009 [P] Add Gitleaks configuration in `.gitleaks.toml`

## Phase 2: Foundational

- [X] T010 Create framework-free Article, Tag, and ArticleQuery rules in `src/main/java/com/conduit/domain/article/`
- [X] T011 Add article/tag Flyway schema in `src/main/resources/db/migration/V2__create_articles.sql`
- [X] T012 Define article persistence port in `src/main/java/com/conduit/application/article/ArticleRepository.java`
- [X] T013 Implement JPA entities/adapters in `src/main/java/com/conduit/infrastructure/article/persistence/` and `src/main/java/com/conduit/infrastructure/user/persistence/`
- [X] T014 Configure article use cases in `src/main/java/com/conduit/application/article/ArticleUseCaseConfiguration.java`
- [X] T015 Implement HTTP mapping and errors in `src/main/java/com/conduit/interfaces/rest/article/`, `src/main/java/com/conduit/interfaces/rest/user/`, and `src/main/java/com/conduit/interfaces/rest/shared/`
- [X] T016 Add architecture boundary test in `src/test/java/com/conduit/architecture/HexagonalArchitectureTest.java`
- [X] T017 Verify generated fixtures and HTTP tests in `src/test/java/com/conduit/article/`

## Phase 3: User Story 1 - Publish and Manage Articles (P1 MVP)

**Goal**: Authenticated authors create, update, and delete their own articles.

**Independent test**: Create tagged content, update fields and tags, verify ownership and slug behavior, then delete through HTTP and PostgreSQL.

- [X] T018 [P] [US1] Verify AC-US1 fixtures in `src/test/java/com/conduit/article/ArticleAcceptanceTest.java` and `ArticleFixtures.java`
- [X] T019 [US1] Implement CRUD, ownership, slug regeneration, and tags in `src/main/java/com/conduit/application/article/`
- [X] T020 [US1] Implement persistence in `src/main/java/com/conduit/infrastructure/article/persistence/ArticleRepositoryAdapter.java`
- [X] T021 [US1] Implement article mutation routes in `src/main/java/com/conduit/interfaces/rest/article/ArticleController.java`
- [X] T022 [US1] Run tagged Cucumber AC-US1 scenarios in `src/test/resources/features/articles.feature`
- [X] T023 [US1] Run PostgreSQL/Testcontainers evidence in `src/test/java/com/conduit/infrastructure/article/persistence/ArticleRepositoryPostgresTest.java`
- [X] T024 [US1] Run Hurl evidence in `conformance/hurl/articles.hurl`, `errors_articles.hurl`, and `errors_authorization.hurl`
- [X] T025 [US1] Run Bruno evidence in `conformance/bruno/articles/`, `errors-articles/`, and `errors-authorization/`
- [X] T026 [US1] Record US1 evidence in `specs/002-articles/traceability.md`

## Phase 4: User Story 2 - Browse and Discover Articles (P1)

**Goal**: Readers retrieve complete single articles and body-free public lists.

**Independent test**: Retrieve, list, filter by author/tag, and inspect anonymous defaults.

- [X] T027 [P] [US2] Verify AC-US2 fixtures in `src/test/java/com/conduit/article/ListArticlesUseCaseTest.java` and `ArticleAcceptanceTest.java`
- [X] T028 [US2] Implement retrieval/list use cases in `src/main/java/com/conduit/application/article/`
- [X] T029 [US2] Implement filtering and projections in `src/main/java/com/conduit/infrastructure/article/persistence/ArticleRepositoryAdapter.java` and `src/main/java/com/conduit/interfaces/rest/article/ArticleResponseMapper.java`
- [X] T030 [US2] Run tagged Cucumber AC-US2 scenarios in `src/test/resources/features/articles.feature`
- [X] T031 [US2] Run PostgreSQL filtering/projection evidence in `src/test/java/com/conduit/infrastructure/article/persistence/ArticleRepositoryPostgresTest.java`
- [X] T032 [US2] Run Hurl article and scoped error evidence in `conformance/hurl/articles.hurl` and `conformance/hurl/articles-errors-scoped.hurl`
- [X] T033 [US2] Run supported Bruno article evidence in `conformance/bruno/articles/`
- [X] T034 [US2] Record US2 evidence in `specs/002-articles/traceability.md`

## Phase 5: User Story 3 - Page Article Results (P2)

**Goal**: Readers receive newest-first bounded pages and field-specific errors.

**Independent test**: Validate limit/offset ordering, totals, invalid values, and a 100-article collection.

- [X] T035 [P] [US3] Verify pagination fixtures in `src/test/java/com/conduit/article/ListArticlesUseCaseTest.java` and `ArticleAcceptanceTest.java`
- [X] T036 [US3] Implement pagination validation in `src/main/java/com/conduit/domain/article/ArticleQuery.java` and `src/main/java/com/conduit/interfaces/rest/article/ArticleController.java`
- [X] T037 [US3] Implement newest-first persistence queries in `src/main/java/com/conduit/infrastructure/article/persistence/ArticleRepositoryAdapter.java`
- [X] T038 [US3] Add the 100-article PostgreSQL proof in `src/test/java/com/conduit/infrastructure/article/persistence/ArticleRepositoryPostgresTest.java`
- [X] T039 [US3] Run tagged Cucumber AC-US3 scenarios in `src/test/resources/features/articles.feature`
- [X] T040 [US3] Run PostgreSQL pagination evidence with `./mvnw verify -P integration`
- [X] T041 [US3] Run Hurl pagination evidence in `conformance/hurl/pagination.hurl` and scoped error contracts
- [X] T042 [US3] Run Bruno pagination evidence in `conformance/bruno/pagination/`
- [X] T043 [US3] Record US3 evidence in `specs/002-articles/traceability.md`

## Phase 6: User Story 4 - Read the Tag Catalogue (P2)

**Goal**: Readers discover normalized tags and empty unknown-tag results.

**Independent test**: Publish tagged articles, list tags, and filter by an unused tag.

- [X] T044 [P] [US4] Verify tag fixtures in `src/test/java/com/conduit/article/ListTagsUseCaseTest.java`
- [X] T045 [US4] Implement normalized tag catalogue in `src/main/java/com/conduit/application/article/`
- [X] T046 [US4] Implement tag persistence and `GET /api/tags` in `src/main/java/com/conduit/infrastructure/article/persistence/ArticleRepositoryAdapter.java` and `src/main/java/com/conduit/interfaces/rest/article/ArticleController.java`
- [X] T047 [US4] Run tagged Cucumber AC-US4 scenarios in `src/test/resources/features/articles.feature`
- [X] T048 [US4] Run PostgreSQL tag evidence in `src/test/java/com/conduit/infrastructure/article/persistence/ArticleRepositoryPostgresTest.java`
- [X] T049 [US4] Run Hurl tag evidence in `conformance/hurl/tags.hurl` and `conformance/hurl/articles.hurl`
- [X] T050 [US4] Run Bruno tag evidence in `conformance/bruno/tags/`
- [X] T051 [US4] Record US4 evidence in `specs/002-articles/traceability.md`

## Phase 7: Polish and Cross-Cutting Concerns

- [X] T052 Add informative JaCoCo reporting and calibration notes in `pom.xml` and `specs/002-articles/research.md`
- [X] T053 Add Actuator health, structured logs, metrics, and `ObservabilityHttpTest.java` in `src/main/resources/application.yml`, `src/main/java/com/conduit/interfaces/rest/article/`, and `src/test/java/com/conduit/interfaces/rest/shared/`
- [X] T054 Run Gitleaks and record the result in `specs/002-articles/traceability.md`
- [X] T055 Keep OWASP Dependency-Check documented as deferred in `pom.xml` until `NVD_API_KEY` exists; exclude it from default `verify` and Lefthook
- [X] T056 Run the reproducible sequence from `specs/002-articles/quickstart.md`, isolating deferred feed/favorite/comment endpoints
- [X] T057 Complete the final FR/AC matrix in `specs/002-articles/traceability.md`
- [X] T058 Run Spotless, Checkstyle, Lefthook pre-commit, and `./mvnw clean verify -P integration`; record final status

## Dependencies & Execution Order

- Phase 1 establishes the toolchain; Phase 2 blocks all stories.
- US1 is the MVP; US2 follows US1; US3 and US4 depend on the shared list foundation.
- Each story follows fixtures -> implementation -> Cucumber -> PostgreSQL -> Hurl -> Bruno -> traceability.
- PostgreSQL/Testcontainers is mandatory when Docker is available. NVD remains a manually activated deferred gate.

## Parallel Opportunities

- T006-T009 can run in parallel after the wrapper exists.
- T018, T027, T035, and T044 can run in parallel because they use separate test files.
- T054 and T058 can run in parallel after application files exist.

## Implementation Strategy

1. Establish Maven, Java, tests, and quality tooling.
2. Deliver US1 as the MVP.
3. Add discovery, pagination, and tags incrementally.
4. Finish observability, traceability, and reproducible validation.

**MVP scope**: Phase 3 / User Story 1.

## Phase 8: Convergence

**Purpose**: Close the remaining evidence and artifact-state gaps found after implementation.

- [X] T059 [HIGH] Run and record a Bruno article-scoped error/authorization validation that excludes deferred feed, favorite, and comment endpoints while preserving the external fixtures in `conformance/bruno/` and recording the result in `specs/002-articles/traceability.md` (partial)
- [X] T060 [MEDIUM] Execute and document the complete reproducible quickstart sequence using the explicit compiled Cucumber feature, PostgreSQL/Testcontainers, scoped Hurl contracts, supported Bruno collections, and the deferred NVD gate in `specs/002-articles/quickstart.md` and `specs/002-articles/traceability.md` (partial)
- [X] T061 [MEDIUM] Reconcile the generated task checklist and execution-status evidence with the already implemented code and passing gates in `specs/002-articles/tasks.md` and `specs/002-articles/traceability.md` without changing `spec.md` or `plan.md` (partial)

## Requirement Mapping

| Requirements | Task coverage |
|---|---|
| FR-001--FR-004, FR-010a, FR-011--FR-014 | T018-T026, T059-T061 |
| FR-005--FR-007, FR-009, FR-013--FR-014 | T027-T034, T059-T061 |
| FR-008, FR-008a, FR-009, FR-012--FR-013, SC-005 | T035-T043, T059-T061 |
| FR-007, FR-010, FR-010a, FR-013, SC-006 | T044-T051, T059-T061 |
| SC-001--SC-004, SC-006 | T018-T058, T060-T061 |
| SC-005 | T038, T040, T043 |
