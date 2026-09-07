---
applyTo: ".github/workflows/**, .github/**/*.yml, .github/**/*.yaml, Dockerfile*, docker-compose.yml, lefthook.yml"
---

# CI/CD and delivery controls

Any automated validation should run the same essential checks that a contributor can reproduce locally: artifact structure, script syntax, validity of structured data, available links and tests.

A workflow must explicitly fail when its prerequisite or primary check fails. Do not mask an error with a `|| true`, a filter that removes exit code, or a step placed only in a path that does not cover its dependencies.

External actions and dependencies must be pinned according to the repository convention. CI secrets are injected by the environment and are never written to logs, fixtures or published artifacts.

A green local result is not enough to declare the CI green: after a push, wait for the conclusion of the remote run and deal with its own conditions, in particular missing caches and generated artifacts.
