---
title: "Outillage Java du projet Conduit"
description: "Catalogue des outils à consolider et à ajouter pour développer, tester, sécuriser et livrer l'API Java/Spring Boot de Conduit."
date: 2026-09-06
status: DRAFT
---

# Outillage Java du projet Conduit

> Ce document transpose la [note sur la boîte à outils du CRM coaching](../../../coaching-craft/03-produits/formation-developpeur-ia/NOTE-OUTILS.md)
> au terrain de ce dépôt : **Java 25, Spring Boot 3.5.5, Maven, PostgreSQL et Flyway**.

## Comment lire ce catalogue

La note source décrit un projet TypeScript et ne constitue donc pas une preuve que ses outils
fonctionnent ici. Ce catalogue distingue :

- **Déjà présent** : visible dans `pom.xml` ou dans les fichiers du dépôt.
- **Prioritaire** : à implémenter pour réduire un risque ou accélérer le cycle quotidien.
- **Nice-to-have** : utile après stabilisation du socle, mais non bloquant pour livrer Conduit.

Un outil n'est considéré comme adopté que lorsqu'il possède une configuration versionnée, une
commande reproductible et un contrôle observable dans la CI ou dans le cycle local.

## Socle prioritaire

### 1. Maven Wrapper

**Rôle.** Garantir que tous les développeurs et tous les runners utilisent la même version de
Maven sans installation globale implicite.

**État.** À vérifier puis à ajouter si `mvnw`, `mvnw.cmd` et `.mvn/wrapper/` ne sont pas présents.

**Implémentation attendue.** Les commandes documentées deviennent `./mvnw verify` et
`./mvnw spring-boot:run`. La CI utilise le wrapper et le cache Maven, jamais une version choisie
par l'image du runner.

**Preuve.** Un poste vierge peut lancer le build avec Java 25 et le wrapper uniquement.

### 2. JUnit 5 + Spring Boot Test

**Rôle.** Tester le domaine, les cas d'utilisation et l'intégration Spring avec le framework
déjà fourni par `spring-boot-starter-test`.

**État.** Déjà présent via `spring-boot-starter-test`.

**Implémentation attendue.** Séparer explicitement les tests unitaires sans contexte Spring,
les tests d'intégration avec `@SpringBootTest` et les tests web ciblés avec `@WebMvcTest`.
Les tests de domaine restent indépendants de Spring et de la base.

**Preuve.** `./mvnw test` exécute les tests unitaires rapidement ; les tests d'intégration sont
identifiables et exécutables par une commande dédiée.

### 3. Testcontainers

**Rôle.** Exécuter les tests d'intégration contre le même moteur PostgreSQL que celui attendu en
production, plutôt que de dépendre d'une base locale ou de H2.

**État.** À ajouter. H2 est actuellement présent pour les tests ; il ne doit pas masquer les
différences PostgreSQL de SQL, transactions et migrations.

**Implémentation attendue.** Ajouter `org.testcontainers:junit-jupiter` et
`org.testcontainers:postgresql`, démarrer PostgreSQL avec `@Testcontainers`, appliquer Flyway
au démarrage et injecter la `DataSource` de test via une configuration dédiée.

**Preuve.** Une suite d'intégration démarre sur une machine sans PostgreSQL local et valide les
migrations Flyway ainsi que les repositories JPA.

### 4. ArchUnit

**Rôle.** Vérifier automatiquement les frontières de l'architecture hexagonale que le compilateur
et les tests fonctionnels ne garantissent pas : le domaine ne dépend ni de Spring, ni de JPA, ni
de l'interface HTTP.

**État.** À ajouter en dépendance de test.

**Implémentation attendue.** Créer des règles sur les packages `domain`, `application`,
`infrastructure` et `interface`. Les dépendances doivent aller vers les ports, jamais l'inverse.
Documenter toute exception dans la règle ou dans un ADR.

**Preuve.** Un test ArchUnit échoue lorsqu'une entité du domaine importe une annotation Spring ou
qu'un cas d'utilisation importe un contrôleur.

### 5. Spotless + Checkstyle ou Error Prone

**Rôle.** Rendre le formatage et les règles de qualité déterministes, sans discussion de style
dans les revues.

**Choix recommandé.** Commencer par **Spotless** pour le formatage automatique. Ajouter ensuite
**Error Prone** ou **Checkstyle** selon les règles réellement nécessaires ; ne pas empiler les
linters avant d'avoir calibré leur bruit.

**État.** À ajouter.

**Implémentation attendue.** Les vérifications sont branchées à `./mvnw verify`, avec une commande
de formatage explicite pour les développeurs. Les règles bloquantes doivent être peu nombreuses,
justifiées et identiques en local et en CI.

**Preuve.** Un fichier mal formaté ou une règle bloquante fait échouer le build de façon
reproductible.

### 6. JaCoCo

**Rôle.** Mesurer la couverture de lignes et de branches afin de repérer les zones non testées.

**État.** À ajouter.

**Implémentation attendue.** Produire un rapport XML/HTML et commencer par un seuil informatif,
non bloquant. Après calibration sur quelques itérations, fixer un seuil de régression adapté au
code métier plutôt qu'un objectif arbitraire global.

**Limite.** La couverture de lignes ne prouve pas la conformité au contrat RealWorld ; elle
complète les tests de comportement, elle ne les remplace pas.

### 7. CI GitHub Actions

**Rôle.** Rejouer le build sur un runner propre et vérifier que les résultats locaux ne dépendent
pas d'artefacts générés ou d'une configuration personnelle.

**État.** À implémenter dans ce dépôt si aucun workflow Java n'existe encore.

**Implémentation attendue.** Un workflow minimal doit installer Java 25, utiliser le Maven Wrapper,
activer le cache Maven, lancer `./mvnw verify`, puis publier les rapports de tests et de couverture.
Un second job peut lancer la conformité Hurl contre PostgreSQL.

**Preuve.** Une pull request ne peut pas être considérée verte sur la seule base d'un build local.

### 8. Vérification de conformité Hurl / Bruno

**Rôle.** Tester le contrat HTTP RealWorld avec un juge externe aux tests écrits par
l'implémentation.

**État.** Les collections de conformance sont présentes dans `conformance/` ; leur exécution CI
reste à brancher et à maintenir comme gate.

**Implémentation attendue.** Démarrer PostgreSQL et l'application, attendre un endpoint de santé,
puis exécuter `conformance/run-api-tests-hurl.sh` ou la collection Bruno. Conserver les scénarios
hors du code de production.

**Preuve.** Les invariants critiques sont vérifiés, notamment `Authorization: Token`, les erreurs
422 et l'absence de `body` dans les listes d'articles.

## Sécurité et dépendances

### 9. OWASP Dependency-Check ou OSV-Scanner

**Rôle.** Détecter les vulnérabilités connues des dépendances Maven et de leurs transitives.

**Choix recommandé.** Commencer par **OWASP Dependency-Check Maven Plugin**, directement intégré à
`verify`, puis évaluer OSV-Scanner si le dépôt veut centraliser le contrôle de plusieurs stacks.

**État.** À ajouter.

**Implémentation attendue.** Produire un rapport exploitable, définir une politique d'exception
avec expiration et éviter de bloquer le développement sur des findings non confirmés avant
calibration.

### 10. Gitleaks

**Rôle.** Empêcher la publication de secrets dans l'historique Git et les pull requests.

**État.** À ajouter en hook local léger et en job CI.

**Implémentation attendue.** Scanner les diffs et les commits, avec une allowlist minimale et
versionnée. Les secrets de test doivent être clairement factices.

### 11. Validation de configuration et secrets

**Rôle.** Échouer tôt lorsqu'une variable d'environnement, une URL de base ou un secret JWT est
absent ou mal formé.

**État.** À implémenter avec la configuration Spring Boot existante (`@ConfigurationProperties` et
validation Jakarta).

**Preuve.** L'application refuse de démarrer avec une configuration invalide, sans afficher la
valeur secrète dans le message d'erreur.

## Diagnostic et exploitation

### 12. Spring Boot Actuator

**Rôle.** Exposer des endpoints de santé et de métriques adaptés au déploiement.

**État.** À ajouter si aucun endpoint de santé équivalent n'est déjà fourni.

**Implémentation attendue.** Activer `/actuator/health` avec une exposition minimale, séparer les
informations publiques des diagnostics internes et protéger les endpoints sensibles.

**Preuve.** La CI et le déploiement disposent d'un signal de readiness fiable qui vérifie aussi la
connexion PostgreSQL lorsque c'est requis.

### 13. Micrometer + Prometheus

**Rôle.** Mesurer les latences, taux d'erreur et volumes d'appels sans ajouter de logs ad hoc.

**État.** Nice-to-have après Actuator.

**Implémentation attendue.** Ajouter les métriques utiles aux endpoints et aux cas d'utilisation,
avec des noms et tags stables. Ne jamais mettre d'identifiant utilisateur ou de secret dans les
labels.

### 14. Logs structurés

**Rôle.** Rendre les logs recherchables et corrélables entre requêtes et traitements.

**État.** Prioritaire pour un déploiement réel ; nice-to-have pour le premier cycle local.

**Implémentation attendue.** Utiliser SLF4J/Logback avec sortie JSON en environnement déployé,
un correlation ID et une politique claire pour les données sensibles.

## Nice-to-haves

Ces outils apportent de la valeur, mais seulement après que `verify`, PostgreSQL réel,
l'architecture, la conformité et la CI soient fiables.

| Outil | Valeur | Condition d'adoption |
|---|---|---|
| **Mutation testing (PIT)** | Vérifie que les tests détectent réellement les modifications | La suite métier est assez stable pour absorber un build plus lent |
| **OpenRewrite** | Automatise les migrations Spring/Jakarta et les refactorings répétitifs | Une migration concrète justifie le coût de configuration |
| **jMolecules** | Rend les concepts d'architecture plus explicites dans le code | Les frontières hexagonales sont déjà établies par ArchUnit |
| **Spring Modulith** | Vérifie et documente les modules applicatifs | Le monolithe contient plusieurs modules métier identifiables |
| **JFR / Java Flight Recorder** | Diagnostique les performances et allocations en production | Un problème de performance est observé, pas supposé |
| **GraalVM Native Build Tools** | Réduit le temps de démarrage et l'empreinte mémoire | Le profil de déploiement le justifie et les contraintes de réflexion sont maîtrisées |
| **OpenTelemetry** | Trace les requêtes entre services et dépendances | Plusieurs composants doivent être corrélés au-delà des logs |
| **Sentry ou équivalent** | Centralise les exceptions avec contexte et release | Un environnement de staging/production existe et les données sont gouvernées |
| **SonarQube / SonarCloud** | Agrège dette, duplication et qualité dans les PR | Les contrôles locaux et CI de base sont déjà peu bruyants |
| **JMeter ou Gatling** | Mesure la tenue en charge avec des scénarios reproductibles | Les objectifs de charge sont connus et mesurables |
| **jOOQ** | Offre un SQL typé lorsque JPA devient une abstraction gênante | Un besoin SQL réel apparaît ; ne pas remplacer JPA par principe |

## Ordre d'implémentation recommandé

### Vague 1 — rendre le build reproductible

1. Maven Wrapper et Java 25 vérifié.
2. JUnit 5 avec séparation unitaires/intégration.
3. Spotless et une première règle de qualité mesurée.
4. Workflow CI avec `./mvnw verify`.

### Vague 2 — rendre le comportement fiable

1. Testcontainers PostgreSQL.
2. Flyway exécuté dans les tests d'intégration.
3. ArchUnit sur les frontières hexagonales.
4. Gate de conformité Hurl/Bruno.

### Vague 3 — rendre le projet durable

1. JaCoCo en signal puis en contrôle de régression.
2. Gitleaks et scan des dépendances.
3. Actuator, logs structurés et métriques utiles.
4. Adoption sélective des nice-to-haves selon un problème observé.

## Critères de sortie du socle

- [ ] Un poste vierge lance `./mvnw verify` avec Java 25.
- [ ] Les tests métier n'ont pas besoin de Spring ou PostgreSQL.
- [ ] Les tests d'intégration utilisent PostgreSQL et Flyway, sans dépendre de H2.
- [ ] Une violation d'architecture échoue via ArchUnit.
- [ ] La CI exécute le même build que le développeur et publie les rapports.
- [ ] La conformité Hurl/Bruno est exécutée sur chaque pull request pertinente.
- [ ] Les vulnérabilités et secrets suivent une politique d'exception traçable.
- [ ] Chaque outil adopté possède un fichier de configuration réel et une commande documentée.

## Sources et limites

- [Note outils du CRM coaching](../../../../coaching-craft/03-produits/formation-developpeur-ia/NOTE-OUTILS.md)
- [`pom.xml`](../../pom.xml) — stack actuellement déclarée
- [`README.md`](README.md) — carte du programme SDD
- [`journal.md`](journal.md) — observations et résultats vérifiés sur le terrain

Les versions, seuils et choix de fournisseurs restent à valider au moment de l'implémentation.
Ce document porte une recommandation de séquencement, pas une preuve d'exécution.