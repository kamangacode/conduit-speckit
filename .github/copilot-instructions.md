# Conduit Spec Kit

Ce dépôt documente une méthode SDD avec GitHub Spec Kit et GitHub Copilot, éprouvée sur le
produit Conduit décrit dans le [PRD](../docs/prd/PRD-conduit.md). Le terrain d'implémentation
pédagogique est Java 25 LTS, Spring Boot et Maven ; les règles d'architecture et de contrat
ci-dessous s'appliquent à ce terrain lorsqu'il est créé.

## Conventions de travail

- Le code Java suit les packages `domain/`, `application/`, `infrastructure/` et `interfaces/`.
- Les adaptateurs HTTP REST résident sous `interfaces/rest/`; `interface/` n'est pas utilisé car
	`interface` est un mot-clé Java.
- Les adapters de persistence résident sous `infrastructure/<domaine>/persistence/` et les
	services techniques propres à un domaine sous `infrastructure/<domaine>/security/` ou un
	sous-package équivalent. Les composants transverses restent sous `infrastructure/config/`.
- Le domaine et les cas d'utilisation utilisent des noms métier en PascalCase pour les types,
	camelCase pour les méthodes et variables, et restent indépendants du framework.
- La persistence Java utilise Spring Data JPA/Hibernate derrière les ports applicatifs ; les
	entités JPA et repositories Spring restent sous `infrastructure/`, jamais dans `domain/`.
- Les commits utilisent Conventional Commits (`feat:`, `fix:`, `docs:`, `test:`, `refactor:`)
	et expliquent le pourquoi lorsque le changement n'est pas évident.
- Le cycle Spec Kit est `/speckit-constitution` -> `/speckit-specify` ->
	`/speckit-clarify` -> `/speckit-plan` -> `/speckit-tests` -> `/speckit-tasks` ->
	`/speckit-implement` -> `/speckit-converge`.

## Invariants du contrat RealWorld

- L'authentification utilise `Authorization: Token <jwt>`, jamais `Bearer`.
- Les erreurs de validation renvoient HTTP `422` avec `{"errors":{"champ":["message"]}}`.
- Les endpoints de liste d'articles ne renvoient pas le champ `body` (règle R-7 du PRD).
- Pour un visiteur anonyme, `following` et `favorited` valent `false`.

Le PRD et la spec RealWorld priment sur toute préférence d'implémentation. Toute divergence
doit être explicitement documentée et couverte par un test de contrat ou d'intégration.

## Sources de vérité

- `.specify/integration.json` définit l'intégration Copilot et le séparateur des commandes.
- `.specify/templates/` définit la structure des artefacts Spec Kit.
- `.github/skills/` définit le comportement des commandes Copilot installées.
- `docs/sdd/` explique la méthode et les observations pédagogiques.
- `docs/prd/` définit le produit Conduit et ses contrats.
