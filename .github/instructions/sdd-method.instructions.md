---
applyTo: ".specify/**, .github/skills/**, docs/sdd/**, specs/**"
---

# SDD Method and Spec Kit

Treat the specification as the source of the intent and the code as its verifiable realization. Preserve the sequence constitution -> specify -> clarify if necessary -> plan -> tests -> tasks -> implement -> converge.

A feature's artifacts live under `specs/NNN-slug/`: `spec.md`, `plan.md`, `test-cases.yaml`, `traceability.md`, `tasks.md` and, if necessary, `research.md`, `data-model.md`, `contracts/` and `checklist.md`. A task must be linked to an explicit requirement or decision.

Use Copilot commands with the separator configured in `.specify/integration.json`, currently `/speckit-...`. Do not invent a point variant or bypass installed skills.

`/speckit-tests` transforms each acceptance scenario into a case `AC-*`, then into a scenario
Cucumber tagged `AC-*` and `FR-*`; any ambiguity is pointed out without being invented. Cucumber proves
internal business scenarios. Hurl remains the external RealWorld contract proof, executed after
Cucumber; Bruno is then verified as derived from Hurl.

Observations on Copilot or Spec Kit behavior should be logged in `docs/sdd/journal.md`. Educational documentation is not a substitute for proof of execution.
