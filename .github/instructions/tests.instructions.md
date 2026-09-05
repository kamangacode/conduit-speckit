---
applyTo: "**/src/test/**, **/*.spec.ts, **/*.test.ts, **/*.sh, specs/**/checklist.md"
---

# Tests et preuves

Un test ou script doit prouver un comportement observable et pouvoir échouer si le comportement régresse. Éviter les assertions tautologiques, les tests qui ne contrôlent pas la sortie et les fixtures qui reproduisent exactement l'implémentation.

Distinguer conformité à un contrat externe et tests de régression internes. Ne pas modifier un oracle ou une fixture externe pour faire passer le résultat ; corriger l'implémentation ou documenter l'écart.

Pour une exigence implementée, relier la preuve à l'artefact concerné. Les scripts shell doivent échouer explicitement en cas d'erreur et ne doivent pas exposer de secrets dans leur sortie.
