# Research: Article Publishing and Discovery

## Decision: Preserve duplicate titles and generate unique slugs

- **Decision**: Accept duplicate article titles and generate a unique slug for every article.
- **Rationale**: The RealWorld Hurl suite creates duplicate titles successfully and asserts that
  their slugs differ. The constitution makes that external contract authoritative.
- **Alternatives considered**: Reject normalized duplicate titles with `409`. Rejected because it
  contradicts `conformance/hurl/errors_articles.hurl` and the feature's own edge-case statement.

## Decision: Keep business and contract evidence independent

- **Decision**: Generate tagged Cucumber scenarios from AC cases; run Hurl only after Cucumber.
- **Rationale**: Cucumber documents and exercises project business behavior. Hurl remains an
  independent RealWorld oracle and cannot be satisfied by an internal test.
- **Alternatives considered**: Use only Hurl or only Cucumber. Rejected because either choice
  loses one of the two independent forms of evidence.

## Decision: Retain H2 and require PostgreSQL when Docker is available

- **Decision**: Keep H2 for fast feedback and run the PostgreSQL/Flyway integration lane through
  Testcontainers whenever Docker is available.
- **Rationale**: This is the established project test-lane decision in ADR 003. A reachable Docker
  daemon followed by a container, migration, connection, or assertion failure must fail the build.
- **Alternatives considered**: Use H2 only or require Docker everywhere. Rejected by ADR 003.

## Decision: Delay durability additions until evidence justifies them

- **Decision**: JaCoCo starts as an informative report. Security scans and operational tooling are
  scheduled only when an observed need identifies their required configuration and acceptance proof.
- **Rationale**: The constitution requires calibrated, observable gates rather than speculative
  tooling.
- **Alternatives considered**: Enable thresholds and operational tools in this feature. Rejected
  because no concrete need or calibration result has been recorded.