---
title: "Developer SDD path with Spec Kit"
description: "An operational guide to install Spec Kit, understand SDD, and implement every Conduit feature."
date: 2026-09-07
status: ACTIVE
audience: "Developer discovering Spec Kit and SDD"
---

# Developer SDD path with Spec Kit

This directory is a working guide. It starts from the Conduit repository, takes a developer who knows neither Spec-Driven Development nor Spec Kit, and leads them to a complete implementation of the API described in the [Conduit PRD](../prd/PRD-conduit.md).

Follow the documents in order. Each step produces versioned evidence or an artifact. Code is not the source of intent: the PRD, feature specifications, and their decisions are.

## Active path

| Step | Document | Expected outcome |
|---:|---|---|
| 1 | [Copilot foundation](01-socle-copilot.md) | Understand Copilot surfaces and repository context files |
| 2 | [SDD method](02-methode-sdd.md) | Distinguish intent, design, and execution |
| 3 | [Install and use Spec Kit](03-speckit.md) | Install the tool and complete a disposable cycle |
| 4 | [Conduit construction site](04-chantier-conduit.md) | Implement every Must feature from the PRD in dependency order |

## Before starting

From the repository root:

```bash
git status --short
java --version
./mvnw --version
```

The implementation stack is Java 25 LTS, Spring Boot, and Maven. Spec Kit commands run in VS Code, in Agent mode. HTTP evidence uses Hurl as an external oracle and Bruno as a derived collection.

## Branch workflow

Every feature starts from `develop`. Do not create a feature branch from `main` or from another feature branch. `develop` is the integration base for feature work. Both `develop` and `main` are protected branches.

Before starting a feature, update the local base and create a dedicated branch:

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feat/<feature-slug>
```

Replace `<feature-slug>` with the feature name, for example `feat/001-user-authentication`. Run the complete Spec Kit cycle on that branch. Keep the branch focused on one feature and do not mix unrelated changes into it.

When the feature is complete and all checks pass, publish the branch and open a pull request against `develop`:

```bash
git push --set-upstream origin feat/<feature-slug>
gh pr create --base develop --head feat/<feature-slug>
```

The pull request is the review gate for the feature specification, implementation, tests, traceability, and external conformance evidence.

## Progress rule

Do not move to the next step because the document has been read. Move on when its exit criterion is observable: configuration tested, cycle executed, artifacts reviewed, or tests passing.

For every feature, the cycle is:

```text
specify -> clarify -> human validation -> plan -> tests -> tasks
  -> analyze -> implement -> converge -> external evidence
```

The details and commands are in [04-chantier-conduit.md](04-chantier-conduit.md).

## Archives

Documents describing the coaching program, industrialization, complementary tooling, comparisons, and detailed references are preserved in [archives](archives/). They provide context and options, but they are not part of the implementation path for Conduit.

## Sources of truth

- Functional requirements and contract: [Conduit PRD](../prd/PRD-conduit.md)
- Feature state: `specs/NNN-slug/`
- Method and commands: [SDD instructions](../../.github/instructions/sdd-method.instructions.md)
- Repository rules: [AGENTS.md](../../AGENTS.md) and [Copilot instructions](../../.github/copilot-instructions.md)
