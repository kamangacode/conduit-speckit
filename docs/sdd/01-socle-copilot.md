---
title: "Level 1 - The database GitHub Copilot"
description: "The four surfaces Copilot, the hierarchy of customization files and which surface honors which. The prerequisite for diagnosing Spec Kit instead of undergoing it."
date: 2026-09-05
status: ACTIVE
effort: "~4 h"
---

# Tier 1 - The database GitHub Copilot

> **Objective**: stop treating Copilot as a black box that we talk to, start to
> treat it like a system that we configure.
>
> **Exit criterion**: 3 correct predictions in a row on the effect of a change in
> configuration, recorded in [`journal.md`](journal.md).

---

## Why does this level come first

Spec Kit doesn't do anything magical. It places structured Markdown files in places that
Copilot knows how to read, and he breaks the work into steps. **All its power comes from the way
of which Copilot consumes context.**

Direct consequence for a coach: when a Spec Kit command produces a mediocre result, he
There are only three possible causes - (1) the input spec is fuzzy, (2) the repository context
is absent or contradictory, (3) the task exceeds the agent's window. Know how to decide between
the three **live, in front of a developer** is what separates a coach from a facilitator
demo. And that requires knowing context mechanics, not slash commands.

---

## 1.0 - The workstation, before everything else

> **This level is observed, it cannot be read.** The three rounds of §1.3 assume a Copilot which
> responds. Until this is established, a failed exercise proves nothing: we do not know if the
> configuration is bad or if the surface is silent. **Check first, predict later.**

### The program's IDE, and why it's non-negotiable

**The program is done in VS Code.** This is not a preference, it is a constraint
mechanism: `.github/prompts/*.prompt.md`, `.github/agents/*.agent.md`,
`.github/skills/*/SKILL.md` and the `.vscode/settings.json` merge - everything the integration poses
Copilot of Spec Kit - are **VS Code** mechanisms. The JetBrains Copilot plugin does not
not honor.

Attempting the landing in IntelliJ produces exactly the error that §1.1 describes as the most
expensive: an instruction written in a file that the surface does not read, and the conclusion
“Copilot isn’t listening.” **Keep IntelliJ to write the Java of [tier 4](04-chantier-conduit.md)
is perfectly healthy; running the SDD cycle there is not.**

### The four checks

```bash
# 1. VS Code and its CLI. If `code` is missing from the PATH, link it from the bundle:
#    ln -sf "/Applications/Visual Studio Code.app/Contents/Resources/app/bin/code" ~/.local/bin/code
code --version

# 2. Copilot Chat. Since VS Code 1.136 it is INTEGRATED WITH THE EDITOR - nothing to install.
# It therefore does not appear in `code --list-extensions`, which only shows the extensions
# user. Looking for it there and seeing nothing is a false negative.
ls "/Applications/Visual Studio Code.app/Contents/Resources/app/extensions/copilot"

# 3. GitHub account for the CLI
gh auth status
```

> **Do not install `GitHub.copilot` from the marketplace on a recent VS Code.** This package
> depends on `github.copilot-chat` in a version older than the built-in, and installs it
> fails by `cannot be downgraded`. The message is confusing because it names an extension
> that we weren't trying to install. There is nothing to do: Copilot is already there.

**4. The only check that doesn't need to be scripted - subscription and session.** No commands
does not establish it reliably: the token `gh` does not have the scopes Copilot, and the API responds `404`
even for an account that has a seat. It must therefore be established in the interface:

1. Open VS Code on `conduit-speckit`.
2. Open the Chat panel, switch to **Agent** mode.
3. Ask a trivia question about the repository, for example *"What files are there under docs/prd/?" »*.

**Entry criteria into the level**: this question is answered. Not a response
*just* - a **simply** answer. If Copilot requests a connection or repositoryrts the absence of
seat, fix this now: nothing that follows in the program works without it.

### What this level assumes has already been done

| Prerequisites | Where he comes from | How do you know it's good? |
|---|---|---|
| Repository `conduit-speckit` with a committed baseline | [README, step 1](README.md) | `git log --oneline -1` returns the baseline commit |
| `uv` and `specify` installed | [README, step 2](README.md) · [step 3, §3.1](03-speckit.md) | `specify check` affiche *Specify CLI is ready to use!* |
| VS Code + Copilot Chat in Agent mode | **this §1.0** | the above trivia question gets answered |
| Spec Kit initialized in repository | **not yet, and that is normal** | nothing to check: `.specify/` only exists after [level 3, §3.2](03-speckit.md) |

The last line is the one that is confusing. **Level 1 is done on a depot where Spec Kit is not
installed**, because it concerns the context mechanics of Copilot - which pre-exists
Spec Kit and he would survive. The files you write in §1.4 are not Spec Kit files:
these are Copilot files, which Spec Kit will then exploit without ever overwriting them.

---

## 1.1 - The four surfaces

Copilot is not one proconduit, it's four proconduits that share a brand. They have neither the
same context, neither the same limits, nor the same configuration files.

| Surface | Where it runs | What it sees | SDD usage |
|---|---|---|---|
| **IDE (VS Code, mode agent)** | Poste local | The open workspace, attached files, MCP tools | **This is where the Spec Kit commands run.** The heart of the program. |
| **Cloud agent** | Runner GitHub | One deposit, one branch, 59 min max | Runs an issue → opens a PR. Target of `/speckit.taskstoissues`. |
| **Code review** | GitHub pull request | The PR challenge | Applies targeted `.instructions.md`. The most underestimated lever. |
| **CLI** | Terminal | The current directory + its own instructions | Non-IDE tasks, scripts, exploration. |

**The mistake that costs the most as a team**: writing an instruction in a file that the
target surface does not read, note that “Copilot is not listening”, and conclude that the
configuration is useless. From there, the team returns to the free prompt and all
benefit of SDD disappears.

**Exercise 1.1** (45 min) - For each of the 4 surfaces, write in
[`journal.md`](journal.md): at what point in the SDD cycle it occurs, and what is its
hard limit. Then check on your tenant which ones are actually activated (the cloud agent
requires a paid plan **and** a policy activated by an organization admin).

---

## 1.2 - The hierarchy of customization files

This is the heart of the landing. Three levels, from the broadest to the most targeted.

### Level 1 - Deposit Instructions

```
.github/copilot-instructions.md
```

Free Markdown, no frontmatter. Applies to **all** requests made in the
context of the deposit. Honored by chat, cloud agent and code review.

This is the direct equivalent of your `CLAUDE.md` to `conduit-fullstack`. Same qualities expected:
short, imperative, without repetition. A 400 line file that repeats the framework doc dilutes
the instructions that matter.

### Level 2 - Path-targeted instructions

```
.github/instructions/<nom>.instructions.md
```

With a **mandatory** frontmatter:

```markdown
---
applyTo: "src/main/java/**/domain/**/*.java"
---

The domain must not depend on Spring annotations. No `@Entity`, `@Service`, or
`@Autowired` under `domain/`. Persistence belongs in `infrastructure/`.
```

`applyTo` takes globs. It's the exact analogue of your modular rules
`.claude/rules/12-backend-hexagonal.md` with their `apps/api/**` scope.

> **The point to check on your tenant, and not to assume**: the GitHub documentation
> indicates that path-targeted instructions are honored by the **cloud agent and code
> review**, while VS Code has its own, broader support. Both documentations do not
> do not describe the same perimeter. **Do not decide from the doc - decide from
> observation** (exercise 1.3). And note it in the playbook: this is exactly the kind of
> detail on which a team loses half a day.

### Niveau 3 - Instructions d'agent

```
AGENTS.md          (anywhere in the repository; the closest one wins)
CLAUDE.md          (racine)
GEMINI.md          (racine)
```

`AGENTS.md` is the cross-tool standard. The fact that Copilot also reads `CLAUDE.md` has a
appreciable practical consequence for you: **a deposit already instrumented for Claude Code is
already partially instrumented for Copilot.** To be checked, and exploited if confirmed.

Nesting is the interesting feature in monorepository: an `AGENTS.md` at the root for
general conventions, another under `services/billing/` for what is only true there. The
closest to the edited file wins.

### Other mechanisms (especially on the VS Code side)

| Mechanism | Chemin | What is it for |
|---|---|---|
| **Prompt files** | `.github/prompts/*.prompt.md` | A reusable prompt, invokable like a command |
| **Custom agents** | `.github/agents/*.agent.md` | A specialized agent, summoned by `@nom` |
| **Chat modes** | Ask / Edit / Agent | Trois postures ; SDD vit en mode **Agent** |
| **MCP** | Configuration VS Code | Gives Copilot external tools |

> **Note Spec Kit**: these first two paths are not trivial. The *commands* layout of
> integration Copilot of Spec Kit writes precisely in `.github/agents/*.agent.md` and
> `.github/prompts/*.prompt.md`. See [tier 3](03-speckit.md).

Complete and up-to-date table: [`archives/reference/copilot-customisation.md`](archives/reference/copilot-customisation.md).

---

## 1.3 - The exercise which validates the level

**The principle**: you do not validate this level by knowing *describe* the files. You validate it
by knowing **predict** their effect. The prediction made *before* the observation is the only one
honest measure of understanding - writing down the prediction after the fact proves nothing.

**Protocole** (3 manches, ~1 h 30) :

For each round: (a) write the prediction in `journal.md`, (b) execute, (c) note
the gap. Three fair rounds in a row = level validated.

| # | Manipulation | What to predict |
|---|---|---|
| 1 | Add in `.github/copilot-instructions.md`: *“All answers start with the path of the file concerned. »* Then ask a question in the chat. | Does the response follow the instructions? Does the instruction survive a second question in the same thread? |
| 2 | Create `.github/instructions/domain.instructions.md` with `applyTo: "**/domain/**"` disallowing Spring annotations. Have Copilot create an entity **under** `domain/`, then one **outside** of `domain/`. | Does the instruction apply in both cases, in just one, or in neither? **This is the round that decides the question of perimeter of level 2.** |
| 3 | Place two **contradictory** instructions: one in `copilot-instructions.md`, one in `AGENTS.md`. Ask a question that triggers both. | Which one wins? Does Copilot signal the conflict or silently choose one? |

> Round 3 is the most instructive and the one that no one does. As a team, the instructions
> contradictory are not a textbook hypothesis: they appear mechanically as soon as
> two people edit the configuration without speaking to each other. **Know what Copilot is doing in this
> case is a coach skill**, because the symptom observed by the developer will be
> “Copilot has become inconsistent”, never “our instructions contradict each other”.

---

## 1.4 - Instrument `conduit-speckit` for real

Once the three sleeves are made, place the actual base of the deposit. It will serve the entire program.

```
conduit-speckit/
├── .github/
│   ├── copilot-instructions.md          # general repository conventions
│   └── instructions/
│       ├── domain.instructions.md       # applyTo: **/domain/**
│       ├── tests.instructions.md        # applyTo: **/src/test/**
│       └── api-contract.instructions.md # applyTo: **/interface/**, **/controller/**
└── AGENTS.md                            # cross-tool entry point
```

**What to put in `copilot-instructions.md`, and nothing else**:

1. What the project is, in three lines, with a link to [the PRD](../prd/PRD-conduit.md).
2. The stack and its version: Java 25 LTS, Spring Boot, Maven or Gradle - slice and write it.
3. Conventions that cannot be guessed from the code: naming, package structure, format of
   commits.
4. **The invariants of the contract RealWorld** that Copilot cannot guess and will be wrong
   systematically about them:
   - the authentication header is `Authorization: Token <jwt>`, **not** `Bearer` - this is
     error number one, because all training corpora say `Bearer`;
   - validation errors return `422` with `{"errors":{"champ":["message"]}}`;
   - article **list** endpoints do not return `body` (rule R-7 of PRD);
   - `following` and `favorited` are `false` for an anonymous visitor.

This fourth point is the most important of the level. **An instruction file is only worth
what is improbable in it.** Writing “the code must be readable” is noise: it is
already the default behavior. Writing "the prefix is `Token`, not `Bearer`" fixes a
real, measurable, repetitive bias. This is the criterion to transmit to the team: *an instruction
which does not correct any default bias has nothing to do in the file.*

**Exercise 1.4** (45 min) - Write the 4 files. Then measure: ask Copilot a
authentication controller **before** and **after** having set the instructions, and compare
both outputs on the sole criterion `Token` vs `Bearer`. Paste the two extracts into
`journal.md`.

---

## Exit criteria - summary

- [ ] The 4 surfaces are described in `journal.md`, with their hard limit and their role SDD.
- [ ] The 3 prediction rounds have passed, with the difference noted for each.
- [ ] The question of perimeter of level 2 (`applyTo`) is **decided by observation**, not
      by the doc, and the answer is written.
- [ ] `conduit-speckit` has its 4 context files.
- [ ] The before/after proof on `Token` vs `Bearer` is in `journal.md`.

→ Next level: [`02-methode-sdd.md`](02-methode-sdd.md)

---

## Sources

- [Add filing instructions for Copilot - GitHub Docs](https://docs.github.com/en/copilot/how-tos/configure-custom-instructions/add-repository-instructions)
- [Cloud agent supports AGENTS.md - GitHub Changelog](https://github.blog/changelog/2025-08-28-copilot-coding-agent-now-supports-agents-md-custom-instructions/)
- [About the cloud agent - GitHub Docs](https://docs.github.com/en/copilot/concepts/agents/coding-agent/about-coding-agent)
- [Customizing Copilot in VS Code](https://code.visualstudio.com/docs/copilot/customization/overview)
- [github/awesome-copilot - sample instructions](https://github.com/github/awesome-copilot)
