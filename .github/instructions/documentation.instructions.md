---
applyTo: "docs/**/*.md, specs/**/*.md, AGENTS.md, .github/**/*.md, .specify/**/*.md"
---

# Documentation comme code

Toute documentation ajoutée doit avoir un titre clair, des liens relatifs valides et un périmètre explicite. Mettre à jour la documentation lorsqu'une convention, un workflow ou un contrat change.

Placer les contenus selon leur intention : `docs/sdd/` pour la méthode et les observations, `docs/prd/` pour le produit et ses contrats, `docs/adr/` pour les décisions structurantes, et `specs/` pour les features en cours.

Éviter la duplication normative : une règle active doit avoir une source de vérité identifiable. Les explications longues restent dans la documentation ; les fichiers Copilot restent courts et impératifs.

Ne jamais transformer une hypothèse en fait. Indiquer les éléments non vérifiés et conserver les exemples reproductibles.
