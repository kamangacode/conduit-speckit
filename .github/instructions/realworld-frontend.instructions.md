---
applyTo: "**/*.tsx, **/*.css, docs/prd/specifications/frontend/**"
---

# Terrain pédagogique frontend RealWorld

Cette instruction est conditionnelle au futur terrain frontend Conduit ; elle ne suppose pas qu'une application web existe dans ce dépôt.

Lorsque des composants frontend sont créés, conserver le markup, les classes et les invariants attendus par la spec RealWorld. Ne pas introduire un design system maison ou renommer les classes contractuelles sans raison documentée.

Le frontend ne parle jamais directement à la base : les données passent par l'API. Les états dépendant de l'utilisateur doivent respecter le contrat, notamment `following`, `favorited` et l'en-tête `Authorization: Token <jwt>`.
