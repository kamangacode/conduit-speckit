## Status

Accepted

## Context

The Java terrain needs a persistence strategy that keeps the domain and use
cases independent from Spring and database APIs while providing a conventional
Spring Boot implementation for PostgreSQL.

## Options Considered

- Spring Data JPA/Hibernate behind application repository ports.
- Direct JDBC adapters using `JdbcTemplate`.
- MyBatis or a repository implementation coupled to SQL mappings.

## Decision

Use Spring Data JPA/Hibernate for infrastructure persistence. Application code
depends on repository ports; infrastructure provides `@Entity` classes,
`JpaRepository` interfaces, transaction boundaries, and mappers between JPA
entities and framework-free domain objects. Flyway remains the schema migration
source of truth and Hibernate runs with `ddl-auto=validate` outside tests.

## Consequences

### Positive

- Domain and use-case tests remain independent of Spring and Hibernate.
- Repository implementations use the standard Spring Boot persistence model.
- Entity mappings and persistence concerns are visibly isolated in infrastructure.

### Negative

- The project carries Hibernate and JPA lifecycle complexity.
- Entity/domain mapping must be maintained explicitly.
- Repository tests need `@DataJpaTest` and an embedded or containerized database.

### Neutral

- The external RealWorld HTTP contract is unchanged.
- Switching persistence technology later remains possible behind the port.