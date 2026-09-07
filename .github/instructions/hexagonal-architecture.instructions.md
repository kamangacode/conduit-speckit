---
applyTo: "**/domain/**/*.java, **/application/**/*.java, **/infrastructure/**/*.java, **/interfaces/**/*.java, **/controller/**/*.java"
---

# Hexagonal architecture and clean architecture

This instruction applies to Conduit's Java/Spring terrain when it exists; it does not transform Spec Kit documents into a runtime application.

Dependencies point inward: `domain` does not depend on any framework, `application` depends on domain ports, `infrastructure` implements domain-grouped adapters (`article`, `user`), and `interfaces/rest` exposes domain-grouped REST controllers (`article`, `user`, `shared`). The domain does not know Spring, JPA, or the database.

Ports are defined on the domain or application side depending on the contract they serve. Use cases do not depend on controllers. Controllers validate and map input, delegate, then map output; they do not contain business logic.

Java terrain persistence uses Spring Data JPA/Hibernate only in `infrastructure`:
an application port is implemented by a transactional adapter which delegates to a `JpaRepository`.
`@Entity` classes, Spring repositories and `Entity <-> domain` mappers remain out of
`domain/` and `application/`. Flyway is the schema source of truth and Hibernate is configured in
validation (`ddl-auto=validate`) outside the test environment. Each JPA adapter has a test
`@DataJpaTest` ; the use cases continue to be tested with duplicates of the port.

Entities, value objects and aggregates protect their invariants. Writes between bounded contexts go through explicit contracts or events, not through direct imports which create hidden coupling.
