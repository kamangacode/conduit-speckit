---
applyTo: "**/*"
---

# Commits, review and decisions

A change should be as simple and local as possible. Look for the root cause, avoid temporary workarounds, and don't modify files unrelated to the intent.

When a commit is requested, use Conventional Commits and an imperative subject that explains the why when it is not obvious. Never create a commit on your own initiative.

Any structuring decision regarding a tool, architecture, contract or workflow must be documented in an ADR before implementation if it is known in advance. A decision that appears during work must be added as soon as it is identified.

A review first checks for behavioral regressions, uncovered requirements, security risks, broken links, and missing evidence. Formulate comments with `issue:`, `suggestion:`, `question:` or `nit:` and distinguish a block from a preference.
