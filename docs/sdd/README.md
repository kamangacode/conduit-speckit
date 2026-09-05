---
title: "Programme SDD — GitHub SpecKit + Copilot sur Conduit"
description: "Montée en compétences Spec-Driven Development avec GitHub SpecKit et GitHub Copilot, éprouvée sur un chantier réel (Conduit / RealWorld, Java + Spring Boot), et kit de coaching pour transmettre la méthode à une équipe."
date: 2026-09-05
status: ACTIVE
audience: "Coach technique, puis l'équipe coachée"
---

# Programme SDD — SpecKit + Copilot

> **Objectif double** : (1) maîtriser le Spec-Driven Development outillé par GitHub SpecKit et
> GitHub Copilot, (2) en sortir un kit transmissible pour coacher une équipe de développeurs.
>
> **Terrain d'entraînement** : l'app **Conduit** (spec RealWorld), spécifiée dans
> [`../prd/PRD-conduit.md`](../prd/PRD-conduit.md), implémentée ici en **Java / Spring Boot**.

---

## Le principe du programme

On n'apprend pas SDD en lisant SDD. On l'apprend en **bouclant le cycle plusieurs fois sur un
vrai chantier**, avec un garde-fou externe qui dit sans complaisance si ce qui est sorti est
juste.

Trois choix de conception en découlent :

1. **Le domaine est figé.** Conduit est un clone de Medium dont la spec est publique et
   exhaustive. Zéro énergie dépensée à comprendre le métier — 100 % sur la méthode.
2. **Le juge est extérieur.** La [suite de conformité Hurl officielle de RealWorld](https://github.com/gothinkster/realworld/tree/main/specs/api)
   n'a pas été écrite par l'agent. Elle passe ou elle ne passe pas. C'est l'antidote au test
   tautologique que produit une IA qui note sa propre copie.
3. **Le cycle tourne trois fois, pas une.** Périmètre volontairement restreint (auth +
   articles) pour boucler `constitution → specify → plan → tasks → implement → converge` en
   entier, plusieurs fois. Boucler une seule fois sur un gros périmètre n'apprend rien : les
   erreurs de méthode se paient trop tard pour être corrigées.

---

## Le point de départ réel

Dans `conduit-fullstack`, un pipeline qui est **déjà** du SDD a été construit à la main :

| Ce qui existe déjà | L'équivalent SpecKit |
|---|---|
| `.claude/rules/` — conventions versionnées avec le code | `.specify/memory/constitution.md` |
| `/dev` → `frame` (cadrage, tier) | `/speckit.specify` (le *quoi* et le *pourquoi*) |
| `/dev` → `spec` (acceptance criteria) | `/speckit.specify` + `/speckit.clarify` |
| `/dev` → `analyze` (analyse technique F-full) | `/speckit.plan` (+ `research.md`) |
| `/dev` → `plan` (items cochables) | `/speckit.tasks` |
| `/dev` → `implement` | `/speckit.implement` |
| `/dev` → `review` + `validate` | `/speckit.analyze` + `/speckit.checklist` |
| `docs/adr/` — décisions tracées | `plan.md` (« every technology choice has documented rationale ») |
| `artifacts/frames/`, `specs/`, `plans/` | `specs/NNN-feature/` |
| Les gates utilisateur obligatoires entre steps | **Le point faible de SpecKit — à remettre soi-même** |

**Ce que ça change** : le concept n'est pas neuf. Ce qui s'apprend ici, c'est (a) l'outillage
standardisé, (b) ce que ce standard fait *mieux* qu'un pipeline maison, et surtout (c) **ce
qu'il fait moins bien**. C'est là que se joue la valeur de coach. Un coach qui ne connaît que
les forces de son outil vend un produit ; un coach qui en connaît les angles morts forme des
gens.

---

## Les six paliers

| # | Palier | Ce qui s'apprend | Ce qui se produit | Effort |
|---|---|---|---|---|
| 1 | [Socle Copilot](01-socle-copilot.md) | Les 4 surfaces, la hiérarchie des fichiers de customisation, qui honore quoi | `github-speckit` instrumenté + preuve de comportement | ~4 h |
| 2 | [Méthode SDD](02-methode-sdd.md) | L'inversion spec/code, spec vs plan vs tasks, pourquoi ce n'est pas du waterfall | Note de synthèse + mapping vers le pipeline maison | ~3 h |
| 3 | [SpecKit, la mécanique](03-speckit.md) | CLI, les 10 commandes, les artefacts, les deux layouts Copilot | SpecKit installé + un cycle jetable bouclé | ~4 h |
| 4 | [Le chantier Conduit](04-chantier-conduit.md) | SDD pour de vrai : 3 itérations, Java/Spring, conformité Hurl | Auth + articles conformes, `specs/` complet | ~16 h |
| 5 | [Industrialisation](05-industrialisation.md) | Cloud agent, code review pilotée, CI, les limites dures | Boucle issue → PR → review automatisée | ~6 h |
| 6 | [Kit de coaching](06-kit-coaching.md) | Transformer une pratique en transmission | Ateliers minutés, grille de maturité, métriques | ~8 h |

**Total ≈ 41 h**, soit 5 à 7 semaines à 6-8 h/semaine. Détail, critères de sortie et calendrier
dans [`00-programme.md`](00-programme.md).

---

## Par où commencer — les 90 premières minutes

Ne pas lire les six paliers d'affilée. Faire **ceci**, aujourd'hui, dans cet ordre :

```bash
cd ~/IdeaProjects/github-speckit

# 1. Créer la baseline.
#    `specify init --force` remplace des fichiers aux chemins en conflit :
#    sans commit de référence, il n'y a aucun retour arrière.
git init && git add -A
git commit -m "chore: PRD Conduit + programme SDD (baseline)"
git branch -M main && git checkout -b chore/speckit-bootstrap

# 2. Installer les prérequis
brew install uv
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git@v1.0.4
specify check          # vérifie les prérequis (git, agents détectés)

# 3. Regarder ce que SpecKit VA poser, avant de le poser
specify init --help
specify integration list | grep -i copilot
```

Puis ouvrir le [palier 1](01-socle-copilot.md) et faire l'exercice 1.1.

> **Ne pas sauter le palier 1 pour aller directement à SpecKit.** SpecKit n'est qu'une façon de
> structurer ce qu'on donne à Copilot. Sans savoir comment Copilot consomme un fichier
> d'instructions, on ne sait pas diagnostiquer pourquoi une commande SpecKit rend un résultat
> médiocre — et le coaching se réduit à « relance, ça passera peut-être ».

**Le seul indicateur qui compte au palier 1** : être capable de modifier un fichier de
customisation et de **prédire à l'avance** ce qui change dans la réponse de Copilot. Tant que
la prédiction échoue, on observe l'outil ; on ne le pilote pas.

---

## Structure du dossier

```
docs/sdd/
├── README.md                    ← la carte + le point de départ
├── 00-programme.md              Objectifs, critères de sortie, calendrier, risques
├── 01-socle-copilot.md          Palier 1
├── 02-methode-sdd.md            Palier 2
├── 03-speckit.md                Palier 3
├── 04-chantier-conduit.md       Palier 4 — le cœur du programme
├── 05-industrialisation.md      Palier 5
├── 06-kit-coaching.md           Palier 6
├── journal.md                   Journal de bord (à remplir en continu, pas à la fin)
├── reference/
│   ├── speckit-cheatsheet.md    Les 10 commandes, les artefacts, les flags CLI
│   ├── copilot-customisation.md Quel fichier, quel chemin, quelle surface l'honore
│   └── anti-patterns.md         Les pièges, avec symptôme et correction
└── playbook/                    Le kit transmissible (rempli au palier 6)
```

---

## Convention de ce dossier

Ce programme est **docs-as-code** : versionné avec le code qu'il gouverne, relu comme du code,
mis à jour au même rythme. Une règle en découle —

> **Le terrain prime sur ce qui est écrit ici.** SpecKit v1.0.4 date du 2 septembre 2026 et
> l'outil bouge vite (v1.0.2 → v1.0.4 en trois jours). Quand une commande ne se comporte pas
> comme décrit, c'est cette doc qui a tort : la corriger dans la même session, et noter l'écart
> dans [`journal.md`](journal.md). Un support de formation qui décrit un outil disparu est pire
> qu'absent — il détruit la confiance de ceux qu'on coache.

**État de l'art vérifié le 2026-09-05** : SpecKit `v1.0.4` (publiée le 2026-09-02) · intégration
Copilot via `--integration copilot` · layout *skills* par défaut
(`.github/skills/speckit-<commande>/SKILL.md`) · layout *commands* en option via
`--integration-options="--commands"`.
