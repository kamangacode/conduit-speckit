# Conduit Spec Kit

Conduit Spec Kit est un laboratoire de développement piloté par les spécifications
(SDD, *Specification-Driven Development*) autour du contrat API
[RealWorld](https://github.com/gothinkster/realworld). Le runtime pédagogique est
une API Java 25, Spring Boot, Spring Data JPA/Hibernate et PostgreSQL.

Le dépôt montre comment relier une intention produit, des décisions d'architecture,
des tâches exécutables, du code et des preuves de validation.

## Prérequis

- Java 25 LTS. Vérifier avec `java -version`.
- Maven 3.9 ou supérieur. Vérifier avec `mvn -version`.
- Docker Desktop avec Docker Compose v2. Vérifier avec `docker compose version`.
- [Hurl](https://hurl.dev/) pour la conformité HTTP.
- [Bruno](https://www.usebruno.com/) ou Bun si les tests Bruno sont utilisés.

Le projet ne contient pas de wrapper Maven. Les commandes ci-dessous utilisent donc
`mvn` installé sur la machine.

## Démarrer PostgreSQL

Le fichier [`docker-compose.yml`](docker-compose.yml) démarre PostgreSQL 16 avec :

- la base `conduit-speckit` ;
- l'utilisateur `conduit` ;
- le port local `5432` par défaut ;
- le volume nommé `conduit-speckit-postgres-data` ;
- un healthcheck `pg_isready`.

Le mot de passe n'est pas stocké dans Git. Préparer un environnement local à partir
du modèle :

```bash
cp .env.example .env
```

Remplacer les valeurs placeholder de `.env`, puis démarrer la base :

```bash
docker compose up -d postgres
docker compose ps
```

Voir les logs ou arrêter le service :

```bash
docker compose logs -f postgres
docker compose down
```

`docker compose down` conserve le volume. Pour supprimer aussi les données locales,
utiliser explicitement `docker compose down -v`.

## Démarrer l'API

Les variables `DATABASE_*` et `JWT_SECRET` sont lues par Spring Boot. Docker Compose
lit automatiquement `.env`, mais Maven ne l'injecte pas dans le processus Java :

```bash
set -a
source .env
set +a
mvn spring-boot:run
```

L'API est alors disponible sur `http://localhost:8080`. La migration Flyway crée le
schéma PostgreSQL au démarrage et Hibernate vérifie ensuite le schéma avec
`ddl-auto=validate`.

Pour utiliser une autre base ou un autre port, définir `DATABASE_URL`,
`DATABASE_USERNAME`, `DATABASE_PASSWORD` et éventuellement `POSTGRES_PORT` dans
l'environnement. Ne jamais committer un secret réel.

## Tester le code

Les tests automatisés utilisent H2 et ne nécessitent pas de PostgreSQL :

```bash
mvn clean test
```

Cette suite couvre notamment les cas d'utilisation d'authentification, les
contrôleurs HTTP, la sécurité des tokens et l'adaptateur de persistance JPA.

Pour un démarrage reproductible avec PostgreSQL :

```bash
set -a
source .env
set +a
docker compose up -d postgres
mvn spring-boot:run
```

Dans un autre terminal, vérifier que le service est prêt avec `docker compose ps`.

## Tester la conformité RealWorld

Les scénarios de référence sont dans [`conformance/hurl/`](conformance/hurl/).
Hurl est la source de vérité des tests HTTP. La collection Bruno est générée à
partir de ces scénarios et sert aux exécutions interactives ou alternatives.

L'API démarrée par défaut sur le port `8080` doit être ciblée explicitement :

```bash
cd conformance
HOST=http://localhost:8080 ./run-api-tests-hurl.sh
```

Pour Bruno :

```bash
HOST=http://localhost:8080 ./run-api-tests-bruno.sh
```

Les scripts peuvent aussi recevoir des dossiers ou fichiers ciblés. Par exemple :

```bash
HOST=http://localhost:8080 ./run-api-tests-hurl.sh hurl/auth.hurl
```

La documentation détaillée de la collection se trouve dans
[`conformance/README.md`](conformance/README.md). Les scénarios couvrent les
articles, l'authentification, les commentaires, les favoris, le feed, la
pagination, les profils, les tags et les erreurs d'autorisation/validation.

## Contrats importants

- L'authentification utilise `Authorization: Token <jwt>`, jamais `Bearer`.
- Les erreurs de validation utilisent HTTP `422` et la forme
  `{"errors":{"champ":["message"]}}`.
- Les listes d'articles ne renvoient pas le champ `body`.
- Pour un visiteur anonyme, `following` et `favorited` valent `false`.
- Les mots de passe et les hash de mots de passe ne sont jamais exposés dans les
  réponses HTTP.
- `JWT_SECRET`, `DATABASE_PASSWORD` et les autres secrets doivent rester dans
  l'environnement local ou dans le gestionnaire de secrets du déploiement.

Ces invariants sont détaillés dans le [PRD](docs/prd/PRD-conduit.md) et les
contrats de la feature dans [`specs/001-user-authentication/contracts/`](specs/001-user-authentication/contracts/).

## Principes Spec Kit et SDD

Le dépôt suit la séquence suivante :

```text
constitution -> specify -> clarify -> plan -> tests -> tasks -> implement -> converge
```

### 1. Constitution

La constitution définit les principes durables du projet : architecture hexagonale,
contrats RealWorld, sécurité, testabilité, traçabilité et conventions de travail.
Elle sert de garde-fou pour les spécifications et les implémentations futures.

### 2. Specify

La commande `/speckit-specify` transforme une demande métier en spécification
observable : personas, scénarios, exigences fonctionnelles et critères de succès.
La spécification décrit le comportement attendu, pas la solution technique.

### 3. Clarify

La commande `/speckit-clarify` pose des questions ciblées lorsque la demande est
ambiguë. Les réponses sont encodées dans la spécification afin qu'un lecteur sans
historique puisse comprendre les choix retenus.

### 4. Plan

La commande `/speckit-plan` traduit la spécification en conception technique :
architecture, modèle de données, contrats, interfaces, dépendances et stratégie
de test. Les décisions structurantes sont documentées dans les ADR appropriés.

### 5. Tests

La commande `/speckit-tests` dérive chaque scénario d'acceptation en cas `AC-*`, en scénario
Cucumber tagué avec `AC-*` et `FR-*`, et initialise la matrice de traçabilité. Elle signale les
ambiguïtés sans inventer de comportement. Cucumber prouve les scénarios métier internes; Hurl
reste l'oracle de contrat externe indépendant.

### 6. Tasks

La commande `/speckit-tasks` produit des tâches ordonnées et traçables. Chaque tâche
se rattache à une exigence, une décision ou une preuve attendue. Les tâches restent
dans `specs/<feature>/tasks.md` et leur état représente l'avancement réel.

### 7. Implement

La commande `/speckit-implement` exécute les tâches en respectant le plan. Le code
est organisé en couches hexagonales : `domain`, `application`, `infrastructure` et
adaptateurs HTTP. Le domaine et les cas d'utilisation restent indépendants de
Spring ; JPA et les repositories Spring restent dans l'infrastructure.

### 8. Converge

La commande `/speckit-converge` compare les artefacts avec le code et les preuves
réellement disponibles. Elle ajoute les tâches manquantes au lieu de déclarer la
feature terminée sur la seule base d'une implémentation partielle.

## Artefacts et sources de vérité

Pour la feature d'authentification courante :

- [`spec.md`](specs/001-user-authentication/spec.md) : intention et exigences ;
- [`plan.md`](specs/001-user-authentication/plan.md) : conception technique ;
- [`test-cases.yaml`](specs/001-user-authentication/test-cases.yaml) : cas d'acceptation dérivés ;
- [`traceability.md`](specs/001-user-authentication/traceability.md) : matrice de preuves ;
- [`tasks.md`](specs/001-user-authentication/tasks.md) : tâches et état ;
- [`data-model.md`](specs/001-user-authentication/data-model.md) : modèle de données ;
- [`contracts/`](specs/001-user-authentication/contracts/) : contrats API ;
- [`quickstart.md`](specs/001-user-authentication/quickstart.md) : parcours de démarrage ;
- [`conformance/`](conformance/) : preuves de conformité HTTP ;
- [`.github/instructions/`](.github/instructions/) : règles applicables au code et aux documents ;
- [`.github/skills/`](.github/skills/) : comportement des commandes Spec Kit.

Les conventions actives doivent rester dans les instructions versionnées, les ADR et
les artefacts de feature. La documentation explique le parcours, mais ne remplace
pas ces sources de vérité.

## Commandes Spec Kit

Dans GitHub Copilot, les commandes utilisent le séparateur `-` configuré dans
[`.specify/integration.json`](.specify/integration.json) :

```text
/speckit-constitution
/speckit-specify
/speckit-clarify
/speckit-plan
/speckit-tests
/speckit-tasks
/speckit-implement
/speckit-converge
```

Pour une nouvelle feature, suivre la séquence constitution, specification,
clarification, plan, tests, tâches, implémentation, puis convergence. Avant de conclure,
exécuter Cucumber, PostgreSQL/Testcontainers lorsque Docker est disponible, Hurl, puis la
synchronisation Bruno, et mettre à jour la matrice de traçabilité.

## Architecture d'exécution

- **Domaine** : règles métier et objets indépendants de Spring.
- **Application** : cas d'utilisation et ports sortants.
- **Infrastructure** : Spring Data JPA, Hibernate, Flyway, PostgreSQL et adaptateurs.
- **HTTP** : contrôleurs, mapping des réponses, gestion d'erreurs et filtre JWT.
- **Tests** : H2 pour la boucle rapide, PostgreSQL Compose et Hurl/Bruno pour la
  validation d'exécution et de contrat.

Les migrations Flyway sont la source de vérité du schéma PostgreSQL. Les entités JPA
ne doivent pas devenir un mécanisme concurrent de création ou de modification du
schéma.

## Dépannage rapide

- **Le compose refuse de démarrer** : vérifier que `POSTGRES_PASSWORD` est défini
  et que le port choisi n'est pas déjà occupé. Utiliser `POSTGRES_PORT=5433` si
  nécessaire, puis adapter `DATABASE_URL`.
- **Spring ne trouve pas la base** : vérifier que `.env` a été chargé dans le shell
  avec `set -a; source .env; set +a` et que `docker compose ps` indique un service
  sain.
- **Le test Hurl ne trouve pas l'API** : vérifier que l'API tourne sur `8080` et
  utiliser `HOST=http://localhost:8080`. Les scénarios ajoutent eux-mêmes le
  préfixe `/api`.
- **Un changement de contrat casse un scénario** : modifier d'abord la
  spécification et le scénario Hurl concernés, puis adapter l'implémentation et la
  collection Bruno générée.

## Statut du projet

Le dépôt est un terrain pédagogique SDD/Spec Kit. Les artefacts de spécification et
les tests existants indiquent le périmètre implémenté ; les tâches encore ouvertes
restent la référence pour mesurer ce qui manque avant de déclarer la feature complète.
