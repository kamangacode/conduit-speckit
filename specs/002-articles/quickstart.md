# Quickstart: Article Publishing and Discovery

## Prerequisites

- Java 25 and Maven Wrapper available after the wave-1 setup task.
- Docker available for the PostgreSQL/Testcontainers lane.
- Hurl available for external RealWorld conformance; Bruno available for the derived collection
  synchronization check.
- Required database and JWT settings supplied through the local environment, never committed.

## Evidence Order

1. Run fast H2 feedback and explicitly selected tagged Cucumber scenarios:

   ```bash
   ./mvnw test \
     -Dcucumber.features=target/test-classes/features/articles.feature \
     -Dcucumber.filter.tags='@AC-US1-001 or @AC-US1-002 or @AC-US1-003 or @AC-US1-004 or @AC-US2-001 or @AC-US2-002 or @AC-US2-003 or @AC-US2-004 or @AC-US3-001 or @AC-US3-002 or @AC-US3-003 or @AC-US4-001 or @AC-US4-002 or @FR-011'
   ```

2. When Docker is available, run PostgreSQL/Testcontainers with Flyway. A failure is blocking:

   ```bash
   ./mvnw verify -P integration
   ```

3. Start the PostgreSQL-backed service, then run the independent Hurl contract suite:

   ```bash
    HOST=http://localhost:8080 ./conformance/run-api-tests-hurl.sh \
       conformance/hurl/articles.hurl conformance/hurl/pagination.hurl \
       conformance/hurl/tags.hurl conformance/hurl/articles-errors-scoped.hurl
   ```

4. Verify the generated Bruno collection only after Hurl passes:

   ```bash
   PATH="/usr/local/opt/node@22/bin:$PATH" \
   HOST=http://localhost:8080 ./conformance/run-api-tests-bruno.sh \
     articles pagination tags errors-articles-scoped
   ```

   The legacy `errors_articles.hurl` and `errors-articles` collections also contain
   feed/favorite/comment requests from iteration 3. They are excluded from this
   article-scoped gate and remain deferred rather than changing the external fixtures.

6. Run the security scan that is available locally:

   ```bash
   gitleaks git --config .gitleaks.toml
   ```

   OWASP Dependency-Check remains a manual deferred gate and requires `NVD_API_KEY`:

   ```bash
   NVD_API_KEY=... ./mvnw -Psecurity org.owasp:dependency-check-maven:check
   ```

5. Record command, result, date, and evidence links for every FR-* and AC-* in
   [traceability.md](traceability.md).

## Expected Results

- Cucumber proves the project business scenarios before external validation.
- Testcontainers proves PostgreSQL schema and persistence behavior when Docker is available.
- Hurl proves the external article contract, including body omission from lists and response
  envelopes.
- Bruno has no behavior of its own that diverges from the supported article-scoped Hurl
   contract. Iteration-3 endpoints remain outside this feature.