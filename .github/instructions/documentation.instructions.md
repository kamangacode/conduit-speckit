---
applyTo: "docs/**/*.md, specs/**/*.md, AGENTS.md, .github/**/*.md, .specify/**/*.md"
---

# Documentation as code

Any added documentation must have a clear title, valid relative links, and an explicit scope. Update documentation when an agreement, workflow or contract changes.

Place the contents according to their intention: `docs/sdd/` for the method and observations, `docs/prd/` for the product and its contracts, `docs/adr/` for the structuring decisions, and `specs/` for the current features.

Avoid normative duplication: an active rule must have an identifiable source of truth. Long explanations remain in the documentation; Copilot files remain short and imperative.

Never turn a hypothesis into fact. Indicate unverified elements and keep reproducible examples.
