---
applyTo: ".specify/**, .github/skills/**, docs/sdd/**, specs/**"
---

# Méthode SDD et Spec Kit

Traiter la spécification comme la source de l'intention et le code comme sa réalisation vérifiable. Préserver la séquence constitution -> specify -> clarify si nécessaire -> plan -> tasks -> implement -> converge.

Les artefacts d'une feature vivent sous `specs/NNN-slug/` : `spec.md`, `plan.md`, `tasks.md` et, si nécessaire, `research.md`, `data-model.md`, `contracts/` et `checklist.md`. Une tâche doit être rattachée à une exigence ou à une décision explicite.

Utiliser les commandes Copilot avec le séparateur configuré dans `.specify/integration.json`, actuellement `/speckit-...`. Ne pas inventer une variante en point ou contourner les skills installés.

Les observations sur le comportement de Copilot ou Spec Kit doivent être consignées dans `docs/sdd/journal.md`. Une documentation pédagogique ne remplace pas une preuve d'exécution.
