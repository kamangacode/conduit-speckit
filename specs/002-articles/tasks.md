# Tasks: Article Publishing and Discovery

**Input**: [spec.md](spec.md), [plan.md](plan.md), [research.md](research.md),
[data-model.md](data-model.md), [contracts/articles.openapi.yaml](contracts/articles.openapi.yaml),
[quickstart.md](quickstart.md), [test-cases.yaml](test-cases.yaml), and
[traceability.md](traceability.md).

**Organization**: Tasks are grouped by user story. Generated test evidence precedes
implementation. For each completed story, execute Cucumber, PostgreSQL/Testcontainers, Hurl,
Bruno synchronization, then update traceability.

## Phase 1: Generated Test Evidence

**Purpose**: Establish the derived acceptance inventory before code changes.

- [X] T001 Validate 13 unique AC cases and FR references for SC-003 in `specs/002-articles/test-cases.yaml` with `ruby scripts/functional-tests.rb validate specs/002-articles`
- [X] T002 Verify every AC-US1-001--AC-US4-002 tag and the FR tags for SC-003 in `src/test/resources/features/articles.feature` with `grep -E '^  @AC-|^  Scenario:' src/test/resources/features/articles.feature`
- [X] T003 Generate the tagged JUnit acceptance skeletons for AC-US1-001--AC-US4-002 in `target/generated-test-sources/com/conduit/generated/GeneratedFunctionalTestCases.java` with `ruby scripts/functional-tests.rb generate specs/002-articles target/generated-test-sources`

## Phase 2: Reproducible Build Foundation

**Purpose**: Complete tooling wave 1 before article implementation.

- [X] T004 Add Maven Wrapper for FR-001--FR-014 in `mvnw` and `.mvn/wrapper/maven-wrapper.properties`, then prove it with `./mvnw --version`
- [X] T005 Configure and validate Java 25 for FR-014 in `pom.xml`, then prove it with `./mvnw --version`
- [X] T006 Separate H2 unit tests and PostgreSQL integration tests for SC-003 in `pom.xml` and `src/test/resources/application-test.yml`, then prove each lane with `./mvnw test` and `./mvnw verify -P integration`
- [X] T007 [P] Add Spotless and record its initial non-blocking quality measurement for SC-003 in `pom.xml`, then prove it with `./mvnw spotless:check`
- [X] T008 Add the reproducible verification workflow for SC-002 in `.github/workflows/verify.yml`, then prove it by a successful GitHub Actions run of `./mvnw verify`

## Phase 3: Shared Article Foundation

**Purpose**: Establish framework-isolated Article concepts, persistence, and HTTP delegation.

- [X] T009 Create the framework-free Article, Tag, and ArticleQuery rules for FR-001, FR-002, FR-002a, FR-008, and FR-010a in `src/main/java/com/conduit/domain/article/`
- [X] T010 Add the article, tag, and article-tag Flyway schema for FR-014 in `src/main/resources/db/migration/V2__create_articles.sql`
- [X] T011 Define article persistence ports and Spring/JPA adapters for FR-001--FR-010 in `src/main/java/com/conduit/application/article/ArticleRepository.java` and `src/main/java/com/conduit/infrastructure/persistence/`
- [X] T012 Configure article use cases for FR-001--FR-010 in `src/main/java/com/conduit/application/article/ArticleUseCaseConfiguration.java`
- [X] T013 Add article response mapping, error mapping, and thin HTTP delegation for FR-011--FR-013 in `src/main/java/com/conduit/api/http/ArticleController.java` and `src/main/java/com/conduit/api/http/ArticleResponseMapper.java`
- [X] T014 Add ArchUnit checks for Constitution II and SC-003 in `src/test/java/com/conduit/architecture/HexagonalArchitectureTest.java`, then prove them with `./mvnw test -Dtest=HexagonalArchitectureTest`

## Phase 4: User Story 1 - Publish and Manage an Article (Priority: P1) MVP

**Goal**: An authenticated author creates, updates, and deletes only their own articles.

**Independent Test**: AC-US1-001--AC-US1-004 and the ownership edge scenario pass through the
HTTP boundary, persist through PostgreSQL, and match the relevant Hurl scenarios.

- [X] T015 [P] [US1] Generate focused JUnit fixtures and tests for AC-US1-001--AC-US1-004 in `src/test/java/com/conduit/article/ArticleAcceptanceTest.java` and `src/test/java/com/conduit/article/ArticleFixtures.java`
- [X] T016 [US1] Implement create, update, delete, slug regeneration, tag semantics, and ownership for FR-001--FR-004 and FR-010a in `src/main/java/com/conduit/application/article/`
- [X] T017 [US1] Implement article persistence and author ownership lookup for FR-001--FR-004 and FR-014 in `src/main/java/com/conduit/infrastructure/persistence/ArticleRepositoryAdapter.java`
- [X] T018 [US1] Implement `POST`, `PUT`, and `DELETE /api/articles` contract mapping for AC-US1-001--AC-US1-004 in `src/main/java/com/conduit/api/http/ArticleController.java`
- [X] T019 [US1] Run tagged Cucumber business scenarios for AC-US1-001--AC-US1-004 and FR-011 in `src/test/resources/features/articles.feature` with `./mvnw test -Dcucumber.features=target/test-classes/features/articles.feature -Dcucumber.filter.tags='@AC-US1-001 or @AC-US1-002 or @AC-US1-003 or @AC-US1-004 or @FR-011'`
- [X] T020 [US1] Run PostgreSQL/Testcontainers and Flyway persistence evidence for AC-US1-001--AC-US1-004 in `src/test/java/com/conduit/article/ArticleRepositoryPostgresTest.java` with `./mvnw verify -P integration`
- [ ] T021 [US1] Run independent Hurl evidence for AC-US1-001--AC-US1-004 and FR-011 in `conformance/hurl/articles.hurl`, `conformance/hurl/errors_articles.hurl`, and `conformance/hurl/errors_authorization.hurl` with `HOST=http://localhost:8080 ./conformance/run-api-tests-hurl.sh conformance/hurl/articles.hurl conformance/hurl/errors_articles.hurl conformance/hurl/errors_authorization.hurl`
- [ ] T022 [US1] Verify Hurl-derived Bruno synchronization for AC-US1-001--AC-US1-004 in `conformance/bruno/articles/` with `./conformance/run-api-tests-bruno.sh articles errors-articles errors-authorization`
- [ ] T023 [US1] Record Cucumber, JUnit, Testcontainers, Hurl, and Bruno execution results for FR-001--FR-004, FR-010a--FR-014, and AC-US1-001--AC-US1-004 in `specs/002-articles/traceability.md`

## Phase 5: User Story 2 - Browse and Discover Articles (Priority: P1)

**Goal**: Readers retrieve public article projections and filter them by author or tag.

**Independent Test**: AC-US2-001--AC-US2-004 pass with complete single representations and
body-free list representations.

- [X] T024 [P] [US2] Generate focused JUnit fixtures and tests for AC-US2-001--AC-US2-004 in `src/test/java/com/conduit/article/ListArticlesUseCaseTest.java`
- [X] T025 [US2] Implement article retrieval and list-query use cases for FR-005--FR-007 and FR-009 in `src/main/java/com/conduit/application/article/`
- [X] T026 [US2] Implement author/tag filtering, list projection, and HTTP retrieval routes for AC-US2-001--AC-US2-004 in `src/main/java/com/conduit/infrastructure/persistence/ArticleRepositoryAdapter.java` and `src/main/java/com/conduit/api/http/ArticleController.java`
- [X] T027 [US2] Run tagged Cucumber business scenarios for AC-US2-001--AC-US2-004 in `src/test/resources/features/articles.feature` with `./mvnw test -Dcucumber.features=target/test-classes/features/articles.feature -Dcucumber.filter.tags='@AC-US2-001 or @AC-US2-002 or @AC-US2-003 or @AC-US2-004'`
- [X] T028 [US2] Run PostgreSQL/Testcontainers filtering and projection evidence for AC-US2-001--AC-US2-004 in `src/test/java/com/conduit/article/ArticleRepositoryPostgresTest.java` with `./mvnw verify -P integration`
- [ ] T029 [US2] Run independent Hurl evidence for AC-US2-001--AC-US2-004 in `conformance/hurl/articles.hurl` and `conformance/hurl/errors_articles.hurl` with `HOST=http://localhost:8080 ./conformance/run-api-tests-hurl.sh conformance/hurl/articles.hurl conformance/hurl/errors_articles.hurl`
- [ ] T030 [US2] Verify Hurl-derived Bruno synchronization for AC-US2-001--AC-US2-004 in `conformance/bruno/articles/` with `./conformance/run-api-tests-bruno.sh articles errors-articles`
- [ ] T031 [US2] Record all execution results for FR-005--FR-007, FR-009, FR-013--FR-014, and AC-US2-001--AC-US2-004 in `specs/002-articles/traceability.md`

## Phase 6: User Story 3 - Page Article Results (Priority: P2)

**Goal**: Readers obtain stable newest-first pages and field-specific invalid-pagination errors.

**Independent Test**: AC-US3-001--AC-US3-003 prove page size, offset, total count, order, and
`422` validation.

- [X] T032 [P] [US3] Generate focused JUnit fixtures and tests for AC-US3-001--AC-US3-003 in `src/test/java/com/conduit/article/ListArticlesUseCaseTest.java`
- [X] T033 [US3] Implement pagination value validation and newest-first query rules for FR-008 and FR-008a in `src/main/java/com/conduit/domain/article/ArticleQuery.java` and `src/main/java/com/conduit/application/article/ListArticlesUseCase.java`
- [X] T034 [US3] Implement paginated persistence queries and HTTP error mapping for AC-US3-001--AC-US3-003 in `src/main/java/com/conduit/infrastructure/persistence/ArticleRepositoryAdapter.java` and `src/main/java/com/conduit/api/http/ArticleController.java`
- [X] T035 [US3] Run tagged Cucumber business scenarios for AC-US3-001--AC-US3-003 in `src/test/resources/features/articles.feature` with `./mvnw test -Dcucumber.features=target/test-classes/features/articles.feature -Dcucumber.filter.tags='@AC-US3-001 or @AC-US3-002 or @AC-US3-003'`
- [X] T036 [US3] Run PostgreSQL/Testcontainers pagination evidence for AC-US3-001--AC-US3-002 in `src/test/java/com/conduit/article/ArticleRepositoryPostgresTest.java` with `./mvnw verify -P integration`
- [ ] T037 [US3] Run independent Hurl evidence for AC-US3-001--AC-US3-003 in `conformance/hurl/pagination.hurl` and `conformance/hurl/errors_articles.hurl` with `HOST=http://localhost:8080 ./conformance/run-api-tests-hurl.sh conformance/hurl/pagination.hurl conformance/hurl/errors_articles.hurl`
- [ ] T038 [US3] Verify Hurl-derived Bruno synchronization for AC-US3-001--AC-US3-003 in `conformance/bruno/pagination/` with `./conformance/run-api-tests-bruno.sh pagination errors-articles`
- [ ] T039 [US3] Record all execution results for FR-008, FR-008a, FR-009, FR-012--FR-013, and AC-US3-001--AC-US3-003 in `specs/002-articles/traceability.md`

## Phase 7: User Story 4 - Read the Tag Catalogue (Priority: P2)

**Goal**: Readers discover normalized tags and receive empty results for unknown tags.

**Independent Test**: AC-US4-001--AC-US4-002 prove tag catalogue and empty filtered lists.

- [X] T040 [P] [US4] Generate focused JUnit fixtures and tests for AC-US4-001--AC-US4-002 in `src/test/java/com/conduit/article/ListTagsUseCaseTest.java`
- [X] T041 [US4] Implement normalized tag catalogue and unknown-tag query behavior for FR-007, FR-010, and FR-010a in `src/main/java/com/conduit/application/article/`
- [X] T042 [US4] Implement tag persistence and `GET /api/tags` mapping for AC-US4-001--AC-US4-002 in `src/main/java/com/conduit/infrastructure/persistence/ArticleRepositoryAdapter.java` and `src/main/java/com/conduit/api/http/ArticleController.java`
- [X] T043 [US4] Run tagged Cucumber business scenarios for AC-US4-001--AC-US4-002 in `src/test/resources/features/articles.feature` with `./mvnw test -Dcucumber.features=target/test-classes/features/articles.feature -Dcucumber.filter.tags='@AC-US4-001 or @AC-US4-002'`
- [X] T044 [US4] Run PostgreSQL/Testcontainers tag persistence evidence for AC-US4-001--AC-US4-002 in `src/test/java/com/conduit/article/ArticleRepositoryPostgresTest.java` with `./mvnw verify -P integration`
- [X] T045 [US4] Run independent Hurl evidence for AC-US4-001--AC-US4-002 in `conformance/hurl/tags.hurl` and `conformance/hurl/articles.hurl` with `HOST=http://localhost:8080 ./conformance/run-api-tests-hurl.sh conformance/hurl/tags.hurl conformance/hurl/articles.hurl`
- [ ] T046 [US4] Verify Hurl-derived Bruno synchronization for AC-US4-001--AC-US4-002 in `conformance/bruno/tags/` with `./conformance/run-api-tests-bruno.sh tags articles`
- [ ] T047 [US4] Record all execution results for FR-007, FR-010, FR-010a, FR-013, and AC-US4-001--AC-US4-002 in `specs/002-articles/traceability.md`

## Phase 8: Durable Project Gates

**Purpose**: Complete tooling wave 3 without unrequested nice-to-haves.

- [ ] T048 Add an informative JaCoCo report for SC-003 in `pom.xml` and prove it with `./mvnw verify jacoco:report`; record the baseline without a blocking threshold in `specs/002-articles/traceability.md`
- [ ] T049 Add Gitleaks configuration and a validation task for FR-013 in `.gitleaks.toml`, then prove it with `gitleaks git --config .gitleaks.toml`
- [ ] T050 Add dependency scanning for FR-013 in `pom.xml`, then prove it with `./mvnw org.owasp:dependency-check-maven:check`
- [ ] T051 Add Actuator health, structured logs, and useful article-operation metrics for SC-002 in `pom.xml` and `src/main/resources/application.yml`, then prove health and metrics with `./mvnw test -Dtest=ObservabilityHttpTest` in `src/test/java/com/conduit/api/http/ObservabilityHttpTest.java`
- [ ] T052 Record the observed need and calibration result before enabling a JaCoCo threshold or any additional nice-to-have for SC-003 in `specs/002-articles/research.md`
- [ ] T053 Run the complete reproducible validation for SC-001--SC-006 in `specs/002-articles/quickstart.md` with `./mvnw verify`, Cucumber, Testcontainers/Flyway, Hurl, and Bruno in that order
- [ ] T054 Complete the final FR-001--FR-014 and AC-US1-001--AC-US4-002 execution matrix in `specs/002-articles/traceability.md`

## Dependencies and Execution Order

- Phase 1 completes before implementation: T001 -> T002 -> T003.
- Phase 2 makes the build reproducible and blocks all article implementation.
- Phase 3 establishes shared Article infrastructure and blocks user-story tasks.
- US1 is the MVP. US2 depends on its published-article persistence. US3 and US4 depend on the
  list-query foundation from US2.
- Each story follows generation/verification -> implementation -> Cucumber -> Testcontainers ->
  Hurl -> Bruno -> traceability. Docker unavailable permits the H2 lane only; Docker available
  with a failed container is a failed task.
- Phase 8 runs after all desired story gates pass. T052 prevents uncalibrated coverage gates and
  unjustified additions.

## Parallel Opportunities

- T007 and T008 can run in parallel after T004--T006.
- T009, T010, and T014 can run in parallel when their touched files do not overlap.
- T015, T024, T032, and T040 can be prepared in parallel after T003.
- The durable tasks T048--T051 can be prepared in parallel after their source files exist; T052
  remains sequential before any new blocking gate.

## Implementation Strategy

1. Complete the reproducible build and shared Article foundation.
2. Deliver US1, execute its full evidence chain, and stop at the MVP checkpoint.
3. Add public browsing, pagination, then tags, executing each story's evidence chain.
4. Run the calibrated durability gates and finish the final traceability matrix.

Every task has a checkbox, sequential identifier, requirement or success-criterion reference,
exact path, and executable proof. Hurl remains a separate external contract gate.