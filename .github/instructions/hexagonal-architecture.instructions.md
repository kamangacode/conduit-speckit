---
applyTo: "**/domain/**/*.java, **/application/**/*.java, **/infrastructure/**/*.java, **/interfaces/**/*.java, **/controller/**/*.java"
---

# Architecture hexagonale et clean architecture

Cette instruction s'applique au terrain Java/Spring de Conduit lorsqu'il existe ; elle ne transforme pas les documents Spec Kit en application runtime.

Les dépendances pointent vers l'intérieur : `domain` ne dépend d'aucun framework, `application` dépend des ports du domaine, `infrastructure` implémente les adaptateurs et `interfaces/rest` expose les contrôleurs REST. Le domaine ne connaît ni Spring, ni JPA, ni la base de données.

Les ports sont définis du côté du domaine ou de l'application selon le contrat qu'ils servent. Les cas d'utilisation ne dépendent pas des contrôleurs. Les contrôleurs valident et mappent l'entrée, délèguent, puis mappent la sortie ; ils ne contiennent pas de logique métier.

La persistence du terrain Java utilise Spring Data JPA/Hibernate uniquement dans `infrastructure` :
un port applicatif est implémenté par un adapter transactionnel qui délègue à un `JpaRepository`.
Les classes `@Entity`, les repositories Spring et les mappers `Entity <-> domain` restent hors de
`domain/` et `application/`. Flyway est la source de vérité du schéma et Hibernate est configuré en
validation (`ddl-auto=validate`) hors environnement de test. Chaque adapter JPA dispose d'un test
`@DataJpaTest` ; les use cases continuent d'être testés avec des doubles du port.

Les entités, value objects et agrégats protègent leurs invariants. Les écritures entre bounded contexts passent par des contrats ou événements explicites, pas par des imports directs qui créent un couplage caché.
