---
title: "Référence — GitHub SpecKit"
description: "Les dix commandes, leurs artefacts, les flags du CLI et les deux layouts de l'intégration Copilot. Vérifié sur SpecKit v1.0.4."
date: 2026-09-05
status: ACTIVE
verified_against: "spec-kit v1.0.4 (publiée le 2026-09-02)"
---

# Référence — GitHub SpecKit

> **Vérifié le 2026-09-05 contre la v1.0.4.** L'outil bouge vite (v1.0.2 → v1.0.4 en trois
> jours). Tout écart constaté se corrige ici **et** se note dans [`../journal.md`](../journal.md).

---

## Installation

```bash
brew install uv

# Épinglé — recommandé pour toute la durée d'un programme de formation
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git@v1.0.4

# Depuis PyPI (moins précis sur la version)
uv tool install specify-cli
```

---

## Le CLI

| Commande | Rôle |
|---|---|
| `specify init <projet>` | Initialise dans un nouveau dossier |
| `specify init --here` | Initialise dans le répertoire courant |
| `specify check` | Vérifie les prérequis et les agents détectés |
| `specify integration list` | Liste les intégrations disponibles (30+ agents) |
| `specify preset search` / `add` / `list` / `remove` | Gère les presets (voir plus bas) |
| `specify preset resolve <template>` | **Dit quel fichier gagne** dans la pile de résolution |
| `specify extension add <id>` | Installe une extension (ex. `agent-context`) |
| `specify extension enable` / `disable <id>` | Active ou désactive sans désinstaller |

### Flags de `init`

| Flag | Effet |
|---|---|
| `--integration <clé>` | L'agent cible. `copilot`, `claude`, `cursor-agent`, `gemini`, `cline`, `zed`… |
| `--integration-options "<opts>"` | Options propres à l'intégration. Pour Copilot : `"--commands"` |
| `--here` | Initialise dans le répertoire courant |
| `--force` | **Remplace les fichiers aux chemins en conflit.** Exige une branche de référence. |
| `--non-interactive` | Sans questions — pour les scripts |

> **`--force` est la seule option dangereuse du lot.** La doc SpecKit le dit explicitement :
> établir la branche de référence d'abord.

---

## Les deux layouts de l'intégration Copilot

| Layout | Obtention | Fichiers posés |
|---|---|---|
| **skills** *(défaut)* | `--integration copilot` | `.github/skills/speckit-<commande>/SKILL.md` |
| **commands** *(layout alternatif)* | Selon la version du CLI et l'intégration ; vérifier `specify init --help` | `.github/agents/*.agent.md`<br>`.github/prompts/*.prompt.md`<br>éventuellement une configuration VS Code |

Dans le CLI vérifié le 2026-09-06, `specify init --help` ne propose pas `--commands` pour
Copilot ; il expose seulement l'option générique `--commands-dir` pour les intégrations qui la
supportent. Ne pas ajouter cette option à une commande sans vérifier son aide locale.

Pour cette installation, les fichiers `.github/skills/speckit-<commande>/SKILL.md` sont donc la
source réelle des commandes Spec Kit. Ils contiennent les mêmes instructions opératoires qu'un
prompt, mais sous le layout `skills`. Les prompts ne sont pas générés automatiquement par la
commande `specify init --here --integration copilot` utilisée dans ce projet.

---

## Presets — la modularité de SpecKit

Collections **empilables et ordonnées par priorité** de templates et de commandes. C'est le
mécanisme qui remplace des rules modulaires, à l'échelle de plusieurs dépôts.

**Pile de résolution**, parcourue à chaque lookup de template (à l'exécution, rien n'est figé) :

```
1. .specify/templates/overrides/       ← surcharges ponctuelles du projet
2. .specify/presets/<id>/templates/    ← presets installés, triés par priorité
3. .specify/extensions/<id>/templates/ ← templates fournis par des extensions
4. .specify/templates/                 ← templates du cœur
```

```bash
specify preset add enterprise-safe --priority 10       # couche de base
specify preset add healthcare-compliance --priority 5  # écrase la précédente
specify preset add --dev ./mon-preset                  # depuis un dossier local
specify preset resolve spec-template                   # qui gagne, et pourquoi
```

- **Priorité basse = l'emporte.**
- Un preset **remplace** par défaut ; les *composition strategies* (dont `wrap`) augmentent au
  lieu d'écraser.
- Les **templates** se résolvent à l'exécution ; les **commandes** s'enregistrent à
  l'installation, dans les répertoires d'agent détectés.
- `constitution-template` suit le même modèle : installer ou réordonner un preset ne réécrit
  **pas** la constitution vivante.

## Extensions

| Extension | Rôle |
|---|---|
| `agent-context` | Gère un bloc `<!-- SPECKIT START -->` / `<!-- SPECKIT END -->` dans `CLAUDE.md`, `.github/copilot-instructions.md`, `AGENTS.md`… `context_files` en synchronise plusieurs. Hooks `after_specify`, `after_plan`. |

```bash
specify extension add agent-context     # PAS installée par `specify init`
```

> **À savoir avant de promettre quoi que ce soit** : *« Spec Kit itself never touches your agent
> context file. This extension is the only thing that does, and it's opt-in. »*

## Le preset `constitution-sync` — piège documenté

Il propage la constitution dans `plan/spec/tasks-template.md` et les docs. **GitHub le
déconseille par défaut** : *« it duplicates the constitution as the source of truth and can
fight the composition stack »*. À n'installer que si l'équipe relit les templates matérialisés
comme des artefacts committés en PR.

---

## L'arborescence après initialisation

```
.specify/
├── memory/
│   └── constitution.md      Les principes du projet — gouverne toutes les générations
├── scripts/                 Scripts de support (branches, chemins)
└── templates/               Gabarits de spec.md, plan.md, tasks.md — ADAPTABLES

specs/
└── NNN-slug/                Un dossier par feature
    ├── spec.md              Le quoi / le pourquoi
    ├── plan.md              Le comment + la justification des choix
    ├── research.md          Les explorations techniques
    ├── data-model.md        Le modèle de données
    ├── contracts/           Contrats d'API / d'interface
    ├── checklists/          Checklists de qualité
    └── tasks.md             Les unités de travail ordonnées
```

---

## Les dix commandes

### Le cycle principal

| Ordre | Commande | Rôle | Produit |
|---|---|---|---|
| 0 | `/speckit.constitution` | Principes gouvernant le projet | `.specify/memory/constitution.md` |
| 1 | `/speckit.specify` | Le **quoi** et le **pourquoi** | `spec.md` + une branche de feature |
| 2 | `/speckit.plan` | Le **comment** | `plan.md`, `research.md`, `data-model.md`, `contracts/` |
| 3 | `/speckit.tasks` | L'**ordre** | `tasks.md` |
| 4 | `/speckit.implement` | L'exécution | Code + tests |
| 5 | `/speckit.converge` | Confronte le code aux artefacts, **rouvre du travail** | Ajouts dans `tasks.md` |

### Les commandes optionnelles

| Commande | Quand | Rôle |
|---|---|---|
| `/speckit.clarify` | Après `specify`, **avant** `plan` | Questions ciblées sur ce qui est sous-spécifié |
| `/speckit.analyze` | Après `tasks` | Cohérence **entre** spec, plan et tasks |
| `/speckit.checklist` | À la demande | Checklist de validation qualité |
| `/speckit.taskstoissues` | Après `tasks` | `tasks.md` → issues GitHub |

> **`/speckit.clarify` est la commande la plus rentable du lot et celle qu'on saute le plus
> souvent.** Sans elle, l'agent comble les silences de la spec par des hypothèses
> *silencieuses* — qui deviennent du code qu'aucun critère d'acceptation ne couvre.

---

## L'ordre recommandé

```
/speckit.constitution     ← une fois par projet, puis enrichi à chaque leçon apprise
        ↓
/speckit.specify          ← une fois par feature
        ↓
/speckit.clarify          ← NE PAS SAUTER
        ↓
   [gate humain]          ← ABSENT DE SPECKIT — à ajouter soi-même
        ↓
/speckit.plan
        ↓
/speckit.tasks
        ↓
/speckit.analyze          ← cohérence entre artefacts
        ↓
/speckit.implement
        ↓
/speckit.converge         ← rouvre ce qui manque
        ↓
   [juge externe]         ← ABSENT DE SPECKIT — suite de conformité, CI
```

**Les deux crochets sont ce que SpecKit ne fournit pas.** Voir
[`../05-industrialisation.md`](../05-industrialisation.md).

---

## Adoption sur un projet existant

Les cinq étapes recommandées par la doc officielle :

1. **Créer une base relisible** — commiter et brancher avant `specify init --here --force`.
2. **Capturer les garde-fous** avec `/speckit.constitution`, à partir de ce qui est **déjà vrai**
   dans le code : README, ADRs, guide de contribution, configuration CI. *« Ne pas inventer de
   standards juste pour remplir le gabarit. »*
3. **Choisir un premier changement borné** — une feature, un bug, une modernisation. Surtout
   pas « documenter le système existant », sauf si l'inventaire est lui-même le livrable.
4. **Dérouler le cycle standard**, en énonçant les frontières de compatibilité (« préserver le
   comportement d'autorisation et les réponses d'API actuelles »).
5. **Décider du modèle de maintenance des specs** : archives immuables d'une feature livrée, ou
   contrats vivants ?

> **Avertissements de la doc** : `--force` remplace des fichiers ; l'initialisation ne réécrit
> pas l'application et n'infère aucune spec du comportement existant ; des règles irréalistes
> dans la constitution créent du bruit, pas des contraintes utiles.

---

## Sources

- [github/spec-kit](https://github.com/github/spec-kit) — v1.0.4, publiée le 2026-09-02
- [Documentation Spec Kit](https://github.github.io/spec-kit/)
- [Référence des intégrations](https://github.github.io/spec-kit/reference/integrations.html)
- [Guide projets existants](https://github.github.io/spec-kit/guides/existing-projects.html)
- [`spec-driven.md` — la philosophie](https://github.com/github/spec-kit/blob/main/spec-driven.md)
