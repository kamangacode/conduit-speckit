# Functional Test Traceability

Feature: [Article publishing and discovery](spec.md)

`test-cases.yaml` derives one case from every acceptance scenario. Cucumber is the internal
business-scenario layer; JUnit covers domain and use-case rules; Testcontainers proves PostgreSQL
and Flyway; Hurl is the independent RealWorld contract oracle; Bruno is checked only as the Hurl
derivative. Execution evidence is recorded below; a matrix row remains `not run` when one of its
required independent lanes has no valid result.

## Execution Evidence

- `./mvnw clean test`: passed, 22 tests, 0 failures.
- `./mvnw clean verify -P integration`: passed, 24 tests, 0 failures; both article and user
	PostgreSQL/Testcontainers tests executed with Flyway.
- `./mvnw -Dtest=ArticleAcceptanceTest,ListArticlesUseCaseTest,ListTagsUseCaseTest test`:
	passed, 9 focused article tests, 0 failures.
- `HOST=http://localhost:8080 ./conformance/run-api-tests-hurl.sh conformance/hurl/articles.hurl`:
	passed, 17 requests.
- The Hurl `pagination.hurl` and `tags.hurl` contracts passed, 11 requests total.
- Hurl `errors_articles.hurl` reached the deferred `/api/articles/feed` assertion and stopped at
	`404` versus its expected `401`; the feed is out of scope for this feature and the Hurl file was
	not changed.
- Cucumber scenarios pass when the compiled feature is selected explicitly with
	`-Dcucumber.features=target/test-classes/features/articles.feature`; the default Maven
	discovery path reports zero scenarios and is therefore not used. Bruno could not start because the local Node runtime is missing
	`libllhttp.9.3.dylib`; no Bruno result is claimed.
- JaCoCo is configured as an informative report only; no coverage threshold is enabled.

## Ambiguities Blocking Test Generation

None. The duplicate-title conflict was resolved in [spec.md](spec.md): duplicate titles are
accepted and receive unique slugs.

## Requirement Matrix

| Requirement | Cucumber | JUnit | Testcontainers | Hurl | Status |
|---|---|---|---|---|---|
| FR-001 | articles.feature: Create an article | ArticleAcceptanceTest#createArticle | ArticleRepositoryPostgresTest#createArticle | articles.hurl | not run |
| FR-002 | AC-US1-001 | ArticleTest#generatesUniqueSlug | ArticleRepositoryPostgresTest#persistsArticle | articles.hurl, errors_articles.hurl | not run |
| FR-002a | AC-US1-002 | ArticleTest#regeneratesSlug | ArticleRepositoryPostgresTest#updatesSlug | articles.hurl | not run |
| FR-003 | AC-US1-002, AC-US1-004 | UpdateArticleUseCaseTest | ArticleRepositoryPostgresTest#updatesArticle | articles.hurl | not run |
| FR-004 | AC-US1-003, Reject unauthenticated or unauthorized article mutations | DeleteArticleUseCaseTest | ArticleRepositoryPostgresTest#deletesArticle | articles.hurl, errors_authorization.hurl | not run |
| FR-005 | AC-US2-001 | GetArticleUseCaseTest | ArticleRepositoryPostgresTest#findsBySlug | articles.hurl, errors_articles.hurl | not run |
| FR-006 | AC-US2-002, AC-US2-004 | ListArticlesUseCaseTest | ArticleRepositoryPostgresTest#listsArticles | articles.hurl | not run |
| FR-007 | AC-US2-003, AC-US4-002 | ListArticlesUseCaseTest#filters | ArticleRepositoryPostgresTest#filters | articles.hurl | not run |
| FR-008 | AC-US3-001, AC-US3-002 | ListArticlesUseCaseTest#paginates | ArticleRepositoryPostgresTest#paginates | pagination.hurl | not run |
| FR-008a | AC-US3-003 | ArticleControllerTest#rejectsInvalidPagination | not applicable | errors_articles.hurl | not run |
| FR-009 | AC-US2-001, AC-US2-002, AC-US2-003, AC-US3-001, AC-US3-002 | ArticleResponseMapperTest | not applicable | articles.hurl, pagination.hurl | not run |
| FR-010 | AC-US4-001, AC-US4-002 | ListTagsUseCaseTest | ArticleRepositoryPostgresTest#listsTags | tags.hurl | not run |
| FR-010a | AC-US1-001, AC-US1-002, AC-US1-004, AC-US4-001 | TagTest#normalizesAndDeduplicates | ArticleRepositoryPostgresTest#normalizesTags | articles.hurl, tags.hurl | not run |
| FR-011 | Reject unauthenticated or unauthorized article mutations | ArticleControllerTest#rejectsMissingToken | not applicable | errors_articles.hurl | not run |
| FR-012 | AC-US1-003, AC-US1-004, AC-US3-003 | ArticleControllerTest#mapsArticleErrors | not applicable | errors_articles.hurl | not run |
| FR-013 | all scenarios | ArticleControllerTest#serializesContract | ArticleRepositoryPostgresTest | articles.hurl, pagination.hurl, tags.hurl, errors_articles.hurl | not run |
| FR-014 | AC-US1-001, AC-US2-004 | ArticleAuthorProjectionTest | ArticleRepositoryPostgresTest#joinsAuthor | articles.hurl | not run |

## Acceptance Matrix

| Acceptance case | Cucumber | JUnit | Testcontainers | Hurl | Status |
|---|---|---|---|---|---|
| AC-US1-001 | articles.feature: Create an article with normalized tags | ArticleAcceptanceTest#createArticle | ArticleRepositoryPostgresTest#createArticle | articles.hurl | not run |
| AC-US1-002 | articles.feature: Update an owned article | ArticleAcceptanceTest#updatesOwnedArticle | ArticleRepositoryPostgresTest#updatesArticle | articles.hurl | not run |
| AC-US1-003 | articles.feature: Delete an owned article | ArticleAcceptanceTest#deletesOwnedArticle | ArticleRepositoryPostgresTest#deletesArticle | articles.hurl | not run |
| AC-US1-004 | articles.feature: Preserve, clear, or reject article tags on update | ArticleAcceptanceTest#preservesOrReplacesTags | ArticleRepositoryPostgresTest#updatesTags | articles.hurl | not run |
| AC-US2-001 | articles.feature: Retrieve a published article anonymously | ArticleAcceptanceTest#getsArticleAnonymously | ArticleRepositoryPostgresTest#findsBySlug | articles.hurl | not run |
| AC-US2-002 | articles.feature: List published articles anonymously | ArticleAcceptanceTest#listsArticlesWithoutBody | ArticleRepositoryPostgresTest#listsArticles | articles.hurl | not run |
| AC-US2-003 | articles.feature: Filter public articles by author and tag | ArticleAcceptanceTest#filtersArticles | ArticleRepositoryPostgresTest#filters | articles.hurl | not run |
| AC-US2-004 | articles.feature: List articles as an authenticated reader | ArticleAcceptanceTest#listsAsReader | ArticleRepositoryPostgresTest#listsArticles | articles.hurl | not run |
| AC-US3-001 | articles.feature: Retrieve the newest first page | ArticleAcceptanceTest#returnsNewestFirstPage | ArticleRepositoryPostgresTest#paginates | pagination.hurl | not run |
| AC-US3-002 | articles.feature: Retrieve an offset page | ArticleAcceptanceTest#returnsOffsetPage | ArticleRepositoryPostgresTest#paginates | pagination.hurl | not run |
| AC-US3-003 | articles.feature: Reject invalid pagination | ArticleAcceptanceTest#rejectsInvalidPagination | not applicable | errors_articles.hurl | not run |
| AC-US4-001 | articles.feature: List the tag catalogue | ArticleAcceptanceTest#listsTags | ArticleRepositoryPostgresTest#listsTags | tags.hurl | not run |
| AC-US4-002 | articles.feature: Filter by an unused tag | ArticleAcceptanceTest#filtersUnknownTag | ArticleRepositoryPostgresTest#filters | articles.hurl | not run |