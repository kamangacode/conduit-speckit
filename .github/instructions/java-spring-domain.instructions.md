---
applyTo: "**/domain/**/*.java, **/application/**/*.java"
---

# Java/Spring educational field: domain and application

This statement only applies to the Java/Spring terrain described in `docs/sdd/04-chantier-conduit.md`; it does not create an active Java architecture in the document repository.

Outbuildings should point inward. The domain remains independent of Spring, JPA, database and technical adapters. Use cases are port dependent and not controller dependent.

Do not place `@Entity`, `@Service`, or `@Autowired` in `domain/`. Business invariants live in aggregates and value objects; adapters implement ports from the infrastructure.
