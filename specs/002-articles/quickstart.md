# Quickstart: Article Publishing and Discovery

## Prerequisites

- Java 25 and Maven Wrapper available after the wave-1 setup task.
- Docker available for the PostgreSQL/Testcontainers lane.
- Hurl available for external RealWorld conformance; Bruno available for the derived collection
  synchronization check.
- Required database and JWT settings supplied through the local environment, never committed.

## Evidence Order

1. Run fast H2 feedback and tagged Cucumber scenarios:

   ```bash
   ./mvnw test -Dgroups='AC-*'
   ```

2. When Docker is available, run PostgreSQL/Testcontainers with Flyway. A failure is blocking:

   ```bash
   ./mvnw verify -P integration
   ```

3. Start the PostgreSQL-backed service, then run the independent Hurl contract suite:

   ```bash
   HOST=http://localhost:8080 ./conformance/run-api-tests-hurl.sh \
     conformance/hurl/articles.hurl conformance/hurl/pagination.hurl \
     conformance/hurl/tags.hurl conformance/hurl/errors_articles.hurl
   ```

4. Verify the generated Bruno collection only after Hurl passes:

   ```bash
   HOST=http://localhost:8080 ./conformance/run-api-tests-bruno.sh articles pagination tags errors-articles
   ```

5. Record command, result, date, and evidence links for every FR-* and AC-* in
   [traceability.md](traceability.md).

## Expected Results

- Cucumber proves the project business scenarios before external validation.
- Testcontainers proves PostgreSQL schema and persistence behavior when Docker is available.
- Hurl proves the external article contract, including body omission from lists and response
  envelopes.
- Bruno has no behavior of its own that diverges from Hurl.