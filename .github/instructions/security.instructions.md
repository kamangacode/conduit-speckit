---
applyTo: "**/*.java, **/*.ts, **/*.tsx, **/*.sh, **/*.yml, **/*.yaml, **/*.json, .github/**, .specify/**"
---

# Security by design

Never commit any secret, token, password, private key, real personal data or sensitive environment value. The examples use obviously fictitious placeholders. Scripts explicitly fail without displaying received secrets.

Secrets and sensitive parameters come from an external configuration validated when the target application starts. A missing or malformed secret should cause an explicit failure, not a silent degraded mode.

Never trust a customer-provided identity, role, or sensitive field. Authority comes from a session or token verified on the server side. Property checks should be part of the query or allowed use case, not an afterthought check.

Any dependencies, CI actions, external URLs, or commands executed by a workflow must be identified and, where the mechanism allows, pinned or committed. Any important security decision is documented in an ADR or the relevant requirement.
