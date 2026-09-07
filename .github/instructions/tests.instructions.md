---
applyTo: "**/src/test/**, **/*.spec.ts, **/*.test.ts, **/*.sh, specs/**/checklist.md"
---

# Tests and evidence

A test or script must demonstrate observable behavior and be able to fail if the behavior regresses. Avoid tautological assertions, tests that do not control the output, and fixtures that exactly replicate the implementation.

Distinguish between compliance with an external contract and internal regression testing. Do not modify an oracle or an external fixture to pass the result; correct the implementation or document the deviation.

For an implemented requirement, link the evidence to the relevant artifact. Shell scripts should explicitly fail on errors and should not expose secrets in their output.
