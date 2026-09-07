---
title: "Install and use GitHub Spec Kit"
description: "Install Spec Kit, initialize the Conduit repository, understand its artifacts, and complete a reproducible first cycle."
date: 2026-09-07
status: ACTIVE
audience: "Developer implementing Conduit"
---

# Install and use GitHub Spec Kit

> This is the third stop in the path. It assumes that the [Copilot foundation](01-socle-copilot.md) and the [SDD method](02-methode-sdd.md) are understood. It prepares the Conduit construction site, but does not implement a business feature yet.

## 3.1 Install the prerequisites

The project uses VS Code to run Spec Kit commands and Java/Spring Boot for implementation. From the repository root:

```bash
code --version
git --version
java --version
./mvnw --version

curl -LsSf https://astral.sh/uv/install.sh | sh
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git@v1.0.4
specify check
```

Keep the version pinned during a development cycle. Update Spec Kit between features, never in the middle of a feature, and verify the update in a disposable repository first.

## 3.2 Initialize the repository

Create a recoverable reference before any initialization that could replace a file:

```bash
git status --short
git add -A
git commit -m "chore: baseline before Spec Kit initialization"
git switch -c chore/speckit-bootstrap
specify init --here --integration copilot
```

The Copilot integration currently used in this repository installs skills under `.github/skills/speckit-*/SKILL.md`. Check the behavior of the installed version instead of assuming that an older tutorial still applies:

```bash
specify init --help
find .specify .github/skills -maxdepth 3 -type f | sort
```

Initialization creates, among other things:

```text
.specify/
├── memory/constitution.md
├── scripts/
└── templates/

.github/skills/speckit-*/SKILL.md
specs/
```

It does not replace existing context files such as `.github/copilot-instructions.md`, `.github/instructions/`, or `AGENTS.md`. Record any observed difference in `docs/sdd/journal.md`.

## 3.3 The cycle and its artifacts

For a feature, always run the cycle in this order:

```text
constitution (once, then amended)
  -> specify -> clarify -> [human validation]
  -> plan -> tests -> tasks -> analyze
  -> implement -> converge -> [external evidence]
```

| Command | Question | Main artifact |
|---|---|---|
| `/speckit-constitution` | Which principles govern the repository? | `.specify/memory/constitution.md` |
| `/speckit-specify` | What is the need and which behaviors are required? | `specs/NNN-slug/spec.md` |
| `/speckit-clarify` | Which ambiguities block proof? | Enriched `spec.md` |
| `/speckit-plan` | How will the need be implemented? | `plan.md`, research, model, contracts |
| `/speckit-tests` | How will each criterion be proven? | `test-cases.yaml`, scenarios, traceability |
| `/speckit-tasks` | In which order should the work be done? | `tasks.md` |
| `/speckit-analyze` | Are the artifacts consistent? | Consistency report |
| `/speckit-implement` | Execute the tasks | Code and evidence |
| `/speckit-converge` | What is actually still missing? | Additional tasks |

The two validations that Spec Kit does not enforce are explicit in this path: review the spec before planning, then run the independent Hurl suite after implementation.

## 3.4 Run a disposable cycle

Before Conduit, validate the mechanics on a feature with no business stakes:

```bash
git switch -c throwaway/speckit-healthcheck
```

In Copilot Chat, in Agent mode:

```text
/speckit-constitution
Training project using Java 25 LTS and Spring Boot. The domain remains independent from the framework. Every API has a contract and an HTTP test.

/speckit-specify
Describe a readiness endpoint that distinguishes a started application from reachable dependencies.
```

Then run `/speckit-clarify`, review `spec.md`, validate the spec, and run `/speckit-plan`, `/speckit-tests`, `/speckit-tasks`, `/speckit-analyze`, `/speckit-implement`, and `/speckit-converge`.

At every stage, check that:

- the spec contains no framework, class, or persistence schema;
- the plan justifies its decisions and adds no behavior;
- each task points to a criterion or decision;
- the proof crosses the real HTTP boundary;
- `converge` compares the result with the artifacts instead of automatically declaring success.

After observing the cycle, delete the disposable branch:

```bash
git switch chore/speckit-bootstrap
git branch -D throwaway/speckit-healthcheck
```

## 3.5 Rules for using Spec Kit on Conduit

1. Read the PRD and local specifications before running a command.
2. Never skip `/speckit-clarify`.
3. Stop after `specify` to have `spec.md` reviewed.
4. When a divergence comes from intent, correct the source artifact, regenerate, and measure.
5. Keep a feature small enough to understand, test, and implement in one session.
6. Keep the `F-*`, `R-*`, `AC-*`, and `FR-*` identifiers in the artifacts to preserve traceability.

## Exit criterion

The developer can explain the role of each artifact, has completed a full disposable cycle, and knows where to stop for human validation. They can then begin the [Conduit construction site](04-chantier-conduit.md).

## Local sources

- [Conduit PRD](../prd/PRD-conduit.md)
- [SDD instructions](../../.github/instructions/sdd-method.instructions.md)
