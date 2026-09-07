---
applyTo: "**/*"
---

# Reproducibility and memory of decisions

Versioned rules are the single source of active conventions. A convention must not be redefined differently in another statement, prompt, documentation, or comment.

Classify each piece of information in the right register: a structuring decision in an ADR, a lesson resulting from a failure in the journal or lessons, a method observation in `docs/sdd/journal.md`, and the state of a cycle in the `specs/` artifacts.

Any new practice must first produce an observable and non-blocking signal, be calibrated on real cases, then become a gate only when its noise and false positives are known.

A reader who does not have the conversation history should be able to reconstruct the intent, scope, and validation of a change from the versioned files.
