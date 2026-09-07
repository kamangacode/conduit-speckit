---
applyTo: "**/*.java, **/*.ts, **/*.tsx, **/*.sh, **/*.mjs"
---

# Clean code and quality

Write short units, named by intention and limited to one responsibility. A method which clearly exceeds one page, which mixes orchestration and business decision-making, or which imposes several levels of reading must be broken down before the review.

Prefer explicit types and contracts over permissive casts, implicit nulls, and validation bypasses. Do not use `any`, a non-zero assertion, or a diagnostic suppression without a documented and verifiable invariant.

Helpers have a verbal name describing their intention. Keep a helper in its original file until it is reused. Add a targeted proof when an extraction contains a non-trivial branch, transformation, or edge case.

Existing lint and formatting rules are safeguards: don't weaken them to push through a change. Any exception must be local, justified and followed by a check which proves that it does not mask a regression.
