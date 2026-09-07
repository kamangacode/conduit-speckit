---
applyTo: "docs/prd/**/*.md, docs/requirements/**/*.md, specs/**/*.md, **/*.spec.ts, **/*.test.ts, **/src/test/**/*.java"
---

# Requirements and traceability

A requirement has a stable identifier, an explicit status, and observable acceptance criteria. The criteria describe a context, an action and an expected result; replace vague wording with verifiable behavior.

A Spec Kit feature links `spec.md`, `plan.md`, `tasks.md` and the produced proofs. A task without an identifiable requirement, decision or technical necessity is undefined scope and must be clarified before implementation.

A requirement marked as implemented must point to the files and proofs that realize it. A link to a nonexistent file or a test that does not control expected behavior does not constitute proof.

The product requirements come from `docs/prd/`, the method comes from `docs/sdd/` and the structuring decisions come from the ADRs. Do not duplicate a requirement across multiple sources without an explicit link between them.
