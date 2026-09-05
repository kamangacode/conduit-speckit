# Conduit Spec Kit

Ce dépôt documente une méthode SDD avec GitHub Spec Kit et GitHub Copilot, éprouvée sur le
produit Conduit décrit dans le [PRD](../docs/prd/PRD-conduit.md). Le terrain d'implémentation
pédagogique est Java 21, Spring Boot et Maven ; les règles d'architecture et de contrat
ci-dessous s'appliquent à ce terrain lorsqu'il est créé.

## Conventions de travail

- Le code Java suit les packages `domain/`, `application/`, `infrastructure/` et `interface/`.
- Le domaine et les cas d'utilisation utilisent des noms métier en PascalCase pour les types,
	camelCase pour les méthodes et variables, et restent indépendants du framework.
- Les commits utilisent Conventional Commits (`feat:`, `fix:`, `docs:`, `test:`, `refactor:`)
	et expliquent le pourquoi lorsque le changement n'est pas évident.
- Le cycle Spec Kit est `/speckit-constitution` -> `/speckit-specify` ->
	`/speckit-clarify` si nécessaire -> `/speckit-plan` -> `/speckit-tasks` ->
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
