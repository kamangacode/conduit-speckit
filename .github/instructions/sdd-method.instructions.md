---
applyTo: ".specify/**, .github/skills/**, docs/sdd/**, specs/**"
---

# Méthode SDD et Spec Kit

Traiter la spécification comme la source de l'intention et le code comme sa réalisation vérifiable. Préserver la séquence constitution -> specify -> clarify si nécessaire -> plan -> tests -> tasks -> implement -> converge.

Les artefacts d'une feature vivent sous `specs/NNN-slug/` : `spec.md`, `plan.md`, `test-cases.yaml`, `traceability.md`, `tasks.md` et, si nécessaire, `research.md`, `data-model.md`, `contracts/` et `checklist.md`. Une tâche doit être rattachée à une exigence ou à une décision explicite.

Utiliser les commandes Copilot avec le séparateur configuré dans `.specify/integration.json`, actuellement `/speckit-...`. Ne pas inventer une variante en point ou contourner les skills installés.

`/speckit-tests` transforme chaque scénario d'acceptation en un cas `AC-*`, puis en scénario
Cucumber tagué `AC-*` et `FR-*`; toute ambiguïté est signalée sans être inventée. Cucumber prouve
les scénarios métier internes. Hurl reste la preuve de contrat RealWorld externe, exécutée après
Cucumber; Bruno est vérifié ensuite comme dérivé de Hurl.

Les observations sur le comportement de Copilot ou Spec Kit doivent être consignées dans `docs/sdd/journal.md`. Une documentation pédagogique ne remplace pas une preuve d'exécution.
