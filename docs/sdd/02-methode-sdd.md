---
title: "Level 2 - The SDD method"
description: "The spec/code inversion, the spec ≠ plan ≠ tasks boundary, the refutation of the waterfall objection, and the mapping to the in-house conduit-fullstack pipeline."
date: 2026-09-05
status: ACTIVE
effort: "~3 h"
---

# Level 2 - The SDD method

> **Objective**: understand the conceptual inversion **before** touching the tool, so as not to
> reduce SDD to “a series of slash commands”.
>
> **Exit criteria**: a 5-minute explanation, without notes, said out loud.

---

## Why this bearing before the tool

A developer who knows *why* catches up with a changing tool. The opposite is false - and
SpecKit released three versions in three days in late August 2026.

There is also a harder reason to learn the method: **resistance to SDD is never only technical.**
No one says "I can't type `/speckit.specify`." The real objections are
“we will waste time writing instead of coding”, “it’s a V-cycle in disguise”, “our
specs will be expired in two weeks.” We don't answer it with a tutorial. It is answered by
having figured out what exactly SDD is moving.

---

## 2.1 - L'inversion

The wording of GitHub, to know word for word:

> *« Code serves specifications. The Proconduit Requirements Document isn't a guide for
> implementation; it's the source that generates implementation. »*

And its corollary, which is the true content of the method:

> *« The specification becomes the primary artifact. Code becomes its expression in a particular
> language and framework. »*
>
> *« Maintaining software means evolving specifications. »*

**What it actually moves.** In classic development, the spec is a scaffolding:
useful during construction, jetty afterwards. The truth migrates to the code from the first line
written, and the gap between the two only grows - this is the *specification-implementation
gap*, the reason why no one trusts a doc older than six months.

SDD removes the gap by removing duality: *“When specifications and implementation plans
generate code, there is no gap - only transformation. »*

**The test which verifies that we have understood**: in an SDD project, when a business rule changes, where
will we edit first? If the answer is "in the code, and I will update the doc
after", the inversion did not take place - we do classic development with files
Markdown and more.

---

## 2.2 - The spec ≠ plan ≠ tasks boundary

This is **the** discriminating skill of this level. The three artifacts answer three
different questions, and mixing them up is the most common failure.

| Artifact | Question | Contient | Ne contient **jamais** |
|---|---|---|---|
| `spec.md` | **What** and **why** | User stories, observable behaviors, acceptance criteria, edge cases | A framework name, a table schema, a method signature |
| `plan.md` | **How** | Stack, architecture, data model, contracts, **and the justification for each choice** | New functional requirements |
| `tasks.md` | **In what order** | Executable, ordered units of work with their dependencies | Architectural decisions not decided upstream |

### The rule that stands alone

> **A spec must remain true if we rewrite the application in another language.**

It's the quickest test and it doesn't mislead. “The user receives a token which
authenticates it on subsequent requests » survives a rewrite in Go. « The `AuthService`
calls `JwtEncoder.encode()`" doesn't survive: it's from the plan that leaked in the spec.

Applied to Conduit, the nuance is instructive: PRD says that the header is
`Authorization: Token <jwt>`. **Is this spec or plan?** It's **spec** - because
that it is a contract observable from the outside, imposed by RealWorld, and that it remains true
regardless of the implementation. On the other hand “we sign the JWT with the `jjwt` library”
is of the plan. The boundary is not “functional vs technical”, it is **observable from
external vs. internal choice**. It's more refined, and that's what makes the subject coachable.

### The two leaks to track down

| Fuite | Symptom | Cost |
|---|---|---|
| **The technique goes back into the spec** | The spec names classes, tables, libs | The spec becomes invalidable by a non-developer: the PO can no longer reread it, so no one rereads it anymore |
| **The functional goes down in the plane** | The plan introduces behavior not present in the spec | This behavior has **no acceptance criteria**. It will be implemented and never verified |

The second is the most dangerous and the least visible. This is precisely what
`/speckit.analyze` seeks: consistency between artifacts.

**Exercise 2.2** (45 min) - Take section 11 of [PRD](../prd/PRD-conduit.md) (the rules
R-1 to R-10) and classify each rule as `spec` or `plan`, with a one-line justification.
R-1 (the slug is generated from the title in kebab-case) is the interesting case: argue the
two positions before slicing. Record in [`journal.md`](journal.md).

---

## 2.3 - “It’s waterfall in disguise” - the rebuttal

The objection will come. A three-step response, from weakest to strongest.

**Time 1 - the cycle is not linear.** GitHub formulates it like this:
*“This process is therefore a 0 → 1, (1', ..), 2, 3, N.”* A first draft (0→1), variants
explored in parallel (1'), then iterative enrichment. The `/speckit.converge` command
exists exactly for that: it compares the real code to the spec and **reopens work**. A
waterfall does not have a command that reopens the previous phase.

**Time 2 - the cost of writing has collapsed.** The waterfall was failing for a reason
economical: revising an 80-page document cost weeks, so we didn't revise,
so the document lied. When reviewing a spec costs ten minutes and the
regeneration of the code follows, the logic is reversed. **SDD is not the waterfall that
would come back; this is what the waterfall would have been if writing had been cheap.**

**Time 3 - the most solid, because it is verifiable.** In waterfall, the spec and the code
diverge because nothing mechanically connects them. In SDD, the spec **generates** the code: the
divergence is seen at the next regeneration. The spec is not a phase document, it is
a living artifact of the repository, versioned, reread in PR, which breaks when it lies.

> **Honesty as a coach - the limits to be stated.** A coach who only presents the strengths loses
> its credibility at the first difficulty encountered by the team. Three real limits:
>
> 1. **On a very large existing one, SDD does not back-document.** The SpecKit doc says this:
> initialization "does not rewrite the application and does not infer specs for the
> existing behavior”. The first project must be **limited**, not “document the
> system”.
> 2. **The cost shifts, it does not disappear.** We spend time writing and clarifying this
> that we spent debugging and redoing. The gain is real but it is *delayed* - it is
> exactly the investment profile that a team under pressure refuses.
> 3. **A fuzzy spec produces false code faster than before.** SDD amplifies the quality of
> intention, in both directions. This is what iteration 3 of [tier 4](04-chantier-conduit.md)
> will show about filters and pagination.

---

## 2.4 - Mapping to what already exists

The `/dev` pipeline of `conduit-fullstack` is artisanal SDD. Ask the mapping explicitly
used twice: it accelerates learning, and it makes the method easier to adopt because it presents
Spec Kit as a formalization of practices the repository already contains, not as a revolution.

| `conduit-fullstack` (`/dev`) | SpecKit | Notable gap |
|---|---|---|
| `.claude/rules/` (21 scoped files) | **Three mechanisms, not one**: constitution + scoped instructions + presets | Your rules make two jobs (governing a *decision* / governing the *writing of a file*) that SpecKit separates. Detail in [§3.6 of level 3](03-speckit.md). |
| `frame` (problem, constraints, tier) | `/speckit.specify` | SpecKit has no concept of **tier** (S / F-lite / F-full). Everything goes through the full cycle. |
| `spec` (acceptance criteria) | `/speckit.specify` + `/speckit.clarify` | `/clarify` is **better**: it asks targeted questions instead of leaving the human guessing what's missing. |
| `analyze` (F-full) | `/speckit.plan` + `research.md` | Equivalent. |
| `plan` (items cochables) | `/speckit.tasks` | Equivalent. |
| `implement` | `/speckit.implement` | Equivalent. |
| `review` + `validate` | `/speckit.analyze` + `/speckit.checklist` | `/analyze` checks **consistency between artifacts**, which the in-house review does not do. A real contribution. |
| ADRs (`docs/adr/`) | The rationale in `plan.md` | The ADR **numbered and permanent** is superior for long memory. To keep in addition. |
| **Mandatory user gates** | *rien* | **Largest gap. See below.** |

### The hole to fill: gates

Your in-house pipeline requires human validation after `frame`, `spec` and `plan`. SpecKit
chain commands together without forcing a stop. On a solo project, it works. **As a team, it's
the main risk**: a developer chains `/specify → /plan → /tasks → /implement` into
twenty minutes and produced 2,000 lines that no one framed, from a spec that
no one read it.

This is the hole that the developer must explicitly fill. Two ways to handle it, to experience at
[level 4](04-chantier-conduit.md):

1. **The social gate**: the spec is pushed to PR and reread *before* `/speckit.plan`. Slow, but
   it is the only one who treats the cause.
2. **The tooled gate**: `/speckit.checklist` generates a spec quality checklist, including the
   validation conditions the passage to the next step. Faster, but the agent remains the judge
   and part.

**Exercise 2.4** (30 min) - Complete this table with two columns: “what SpecKit does
better” and “what the in-house device does better”. This list is the raw material of
your legitimacy as a coach: it proves that you evaluated the tool instead of reciting it.

---

## 2.5 - The deliverable of the landing

A summary note in `journal.md`, which answers five questions **without looking at this
file**:

1. What is the inversion, in a sentence?
2. What is the spec/plan boundary, and what test is the slice?
3. Why it’s not waterfall - three arguments?
4. What are the three honest limits of SDD?
5. What is the gap in SpecKit compared to a pipeline with gates, and how to fill it?

Then say the whole thing out loud, stopwatch in hand. **Five minutes maximum.** Beyond that, the
understanding exists but is not condensed - and an uncondensed understanding does not
does not transmit: it is recited.

---

## Exit criteria - summary

- [ ] Rules R-1 to R-10 of PRD are classified spec / plan, with justification.
- [ ] Case R-1 (slug) is argued on both sides before being decided.
- [ ] The mapping table is completed with the two comparative columns.
- [ ] The five answers take 5 minutes out loud, without notes.

→ Palier suivant : [`03-speckit.md`](03-speckit.md)

---

## Sources

- [`spec-driven.md` - the SDD philosophy, github/spec-kit repository](https://github.com/github/spec-kit/blob/main/spec-driven.md)
- [Spec Kit Documentation](https://github.github.io/spec-kit/)
- [Adopt Spec Kit on an existing project](https://github.github.io/spec-kit/guides/existing-projects.html)
