---
title: "Palier 3 — SpecKit, la mécanique"
description: "Installation, initialisation, les deux layouts Copilot, les dix commandes et leurs artefacts. Un cycle jetable bouclé de bout en bout avant de toucher à Conduit."
date: 2026-09-05
status: ACTIVE
effort: "~4 h"
---

# Palier 3 — SpecKit, la mécanique

> **Objectif** : connaître l'outil de bout en bout, y compris ce qu'il ne fait pas.
>
> **Critère de sortie** : un cycle complet bouclé sur une feature **jetable**, tous les
> artefacts produits et lus, puis jetés.

---

## 3.1 — Installation

SpecKit est un CLI Python distribué par `uv`. Il pose des fichiers dans le dépôt et disparaît :
ce n'est pas un runtime, c'est un générateur de contexte.

```bash
# uv, le gestionnaire d'outils Python
brew install uv

# SpecKit, ÉPINGLÉ. Ne pas installer la branche flottante.
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git@v1.0.4

# Vérifier les prérequis et les agents détectés
specify check
```

> **Pourquoi épingler.** v1.0.2, v1.0.3 et v1.0.4 sont sorties entre le 31 août et le
> 2 septembre 2026. Sur un programme de six semaines, une mise à jour au milieu d'une itération
> transforme un échec de méthode en énigme d'outillage — on ne sait plus si le résultat a changé
> parce que la spec était mauvaise ou parce que le template a bougé. **Épingler pendant tout le
> programme, mettre à jour entre deux paliers, jamais au milieu.** Cette règle vaut aussi pour
> l'équipe coachée : c'est la première ligne du playbook.

Une installation depuis PyPI existe (`uv tool install specify-cli`), mais elle ne permet pas
d'épingler aussi explicitement. Préférer la forme Git avec le tag.

---

## 3.2 — Initialisation, et le choix de layout

```bash
cd ~/IdeaProjects/github-speckit
git checkout -b chore/speckit-bootstrap        # la baseline, impérativement

specify init --here --integration copilot
```

`--here` initialise dans le répertoire courant au lieu de créer un sous-dossier. `--force`
existe pour écraser des fichiers en conflit — **c'est pour ça que la branche de référence est
obligatoire**, la doc SpecKit l'indique explicitement.

### Les deux layouts de l'intégration Copilot

C'est une subtilité récente qu'il faut connaître, parce que la plupart des tutoriels en ligne
décrivent l'ancien comportement.

| Layout | Comment l'obtenir | Ce qui est écrit |
|---|---|---|
| **skills** (défaut) | `--integration copilot` | `.github/skills/speckit-<commande>/SKILL.md` |
| **commands** (opt-in) | `--integration copilot --integration-options="--commands"` | `.github/agents/*.agent.md` + `.github/prompts/*.prompt.md` + un merge dans `.vscode/settings.json` |

**Lequel choisir pour ce programme : `--commands`.** Deux raisons.

1. **Pédagogique** : le layout *commands* rend les fichiers directement lisibles comme des
   prompts. Ouvrir `.github/prompts/speckit.specify.prompt.md` et lire ce que la commande
   demande réellement à l'agent est l'un des exercices les plus formateurs du programme — on
   cesse de voir une commande magique, on voit un prompt structuré qu'on pourrait écrire
   soi-même. C'est *exactement* ce qu'un coach doit pouvoir montrer.
2. **Alignement** : `.github/prompts/` et `.github/agents/` sont les mécanismes Copilot
   standards vus au [palier 1](01-socle-copilot.md). Le layout *commands* réutilise ce que
   l'équipe apprend par ailleurs, au lieu d'introduire un troisième concept.

**Exercice 3.2** (30 min) — Initialiser **une fois avec chaque layout**, dans deux branches
jetables. Comparer les arborescences avec `git status`. Noter dans [`journal.md`](journal.md)
ce que chacun pose exactement. Puis garder `--commands` et supprimer l'autre branche.

### Ce que l'initialisation produit

```
.specify/
├── memory/
│   └── constitution.md      # les principes du projet — LE fichier qui gouverne tout
├── scripts/                 # scripts de support (création de branche, chemins)
└── templates/               # les gabarits de spec.md, plan.md, tasks.md

.github/
├── agents/                  # (layout commands) les commandes speckit
└── prompts/                 # (layout commands) leurs prompts

specs/                       # vide au départ : une feature = un dossier NNN-slug/
```

---

## 3.3 — Les dix commandes

Cinq forment le cycle principal, cinq sont optionnelles mais font une grande partie de la
valeur réelle.

### Le cycle principal

| Ordre | Commande | Rôle | Produit |
|---|---|---|---|
| 0 | `/speckit.constitution` | Les principes gouvernant le projet | `.specify/memory/constitution.md` |
| 1 | `/speckit.specify` | Le **quoi** et le **pourquoi** | `specs/NNN-slug/spec.md` + une branche |
| 2 | `/speckit.plan` | Le **comment** : stack, architecture, contrats | `plan.md`, `research.md`, `data-model.md`, `contracts/` |
| 3 | `/speckit.tasks` | L'**ordre** : unités exécutables | `tasks.md` |
| 4 | `/speckit.implement` | L'exécution | Le code et ses tests |
| 5 | `/speckit.converge` | Confronte le code réel aux artefacts et **rouvre du travail** | Ajouts dans `tasks.md` |

### Les commandes optionnelles — là où se joue la qualité

| Commande | Quand | Pourquoi elle compte |
|---|---|---|
| `/speckit.clarify` | Après `specify`, **avant** `plan` | Pose des questions ciblées sur ce qui est sous-spécifié. **La commande la plus rentable de tout le lot.** |
| `/speckit.analyze` | Après `tasks` | Vérifie la cohérence **entre** spec, plan et tasks. Détecte le fonctionnel qui a fui dans le plan. |
| `/speckit.checklist` | À la demande | Génère une checklist de validation qualité. Le support d'un gate outillé. |
| `/speckit.taskstoissues` | Après `tasks` | Convertit `tasks.md` en issues GitHub → cloud agent. Voir [palier 5](05-industrialisation.md). |

> **La commande à ne jamais sauter est `/speckit.clarify`.** Sans elle, l'agent comble les
> silences de la spec par des hypothèses **silencieuses** — et une hypothèse silencieuse
> devient du code non testé, parce qu'aucun critère d'acceptation ne la couvre. Avec elle, les
> silences deviennent des questions explicites auxquelles un humain répond. C'est le mécanisme
> qui distingue le plus nettement SDD du prompt élaboré, et c'est le premier réflexe à ancrer
> chez l'équipe.

Détail complet : [`reference/speckit-cheatsheet.md`](reference/speckit-cheatsheet.md).

---

## 3.4 — Le cycle jetable

**La règle du palier : ne pas commencer par Conduit.** La première exécution sert à découvrir
la mécanique. Y mêler un enjeu réel brouille deux apprentissages en un — quand quelque chose
coince, on ne sait plus si c'est la méthode, l'outil ou le domaine.

**Le sujet** : un endpoint de santé. Trivial, vérifiable en trois secondes, sans piège métier.

```bash
git checkout -b throwaway/speckit-tour
```

Puis, dans le chat Copilot en **mode Agent** :

```
/speckit.constitution

Projet d'entraînement, Java 21 + Spring Boot. Principes :
1. Toute API a un contrat OpenAPI avant son implémentation.
2. Tout endpoint a un test d'intégration qui passe par la couche HTTP réelle.
3. Le domaine ne dépend d'aucune annotation de framework.
```

```
/speckit.specify

Un endpoint de santé qui indique si l'application est prête à servir du trafic,
en distinguant l'application démarrée de ses dépendances joignables.
```

Puis `/speckit.clarify`, `/speckit.plan`, `/speckit.tasks`, `/speckit.analyze`,
`/speckit.implement`, `/speckit.converge`.

### Ce qu'il faut observer — l'exercice réel

Le but n'est pas d'obtenir un endpoint. C'est de **lire ce que chaque commande produit** et de
répondre à ces questions dans `journal.md` :

| Étape | La question à se poser |
|---|---|
| `constitution` | Le fichier généré contient-il des principes que je n'ai pas donnés ? SpecKit invente-t-il ? |
| `specify` | La spec parle-t-elle de Spring, de classes, de `/actuator` ? **Si oui, du plan a fui dans la spec** — le noter, c'est le défaut le plus fréquent. |
| `clarify` | Combien de questions ? Lesquelles auraient produit du code faux si je n'y avais pas répondu ? |
| `plan` | Chaque choix technique est-il **justifié** ? Ou juste affirmé ? |
| `tasks` | Les tâches sont-elles exécutables isolément ? Combien de temps prendrait la plus grosse ? (Retenir ce chiffre : la limite de 59 min du cloud agent arrive au palier 5.) |
| `analyze` | Trouve-t-il de vraies incohérences, ou produit-il un rapport de complaisance ? |
| `implement` | Le code respecte-t-il la constitution — en particulier « pas d'annotation de framework dans le domaine » ? |
| `converge` | Rouvre-t-il du travail réel, ou déclare-t-il tout conforme ? |

> **Le test décisif du palier** : sur `analyze` et `converge`, un agent a une pente naturelle à
> valider son propre travail. Si les deux rapports sont vides du premier coup sur une feature
> qu'on a *volontairement* sous-spécifiée, c'est une information capitale pour le coaching —
> elle dit que ces commandes ne peuvent pas servir de gate de qualité et qu'il faut un juge
> externe. C'est précisément l'hypothèse que le [palier 4](04-chantier-conduit.md) va tester
> avec la suite Hurl.

**Puis tout jeter** :

```bash
git checkout chore/speckit-bootstrap
git branch -D throwaway/speckit-tour
```

Ce qui reste, ce sont les notes. C'est ce qu'on venait chercher.

---

## 3.5 — Lire les prompts sous les commandes

Trente minutes qui valent le reste du palier. Ouvrir :

```
.github/prompts/speckit.specify.prompt.md
.github/prompts/speckit.clarify.prompt.md
.github/prompts/speckit.plan.prompt.md
```

Et les lire comme du code.

**Ce qu'on y découvre** : une commande SpecKit est un prompt structuré, avec des instructions
explicites sur ce qu'il faut demander, ce qu'il faut refuser d'inventer, et le format de
sortie attendu. Rien de magique. **C'est reproductible, adaptable, et améliorable.**

Pour un coach, c'est le moment décisif de démystification. Un développeur qui a lu ces fichiers
comprend d'un coup pourquoi la qualité de sa spec conditionne tout, et il gagne la capacité
d'**adapter** les templates aux conventions de son équipe — ce que fait le dossier
`.specify/templates/`.

**Exercice 3.5** (30 min) — Lire les trois prompts. Repérer dans `speckit.specify.prompt.md`
l'instruction qui interdit d'inventer des détails techniques. Noter sa formulation exacte dans
`journal.md` : elle servira d'argument au palier 6, quand un développeur objectera que « la
spec, c'est trop vague pour coder ».

---

## Critère de sortie — récapitulatif

- [ ] SpecKit v1.0.4 installé et **épinglé**.
- [ ] Les deux layouts comparés, `--commands` retenu et justifié.
- [ ] Un cycle complet bouclé sur la feature jetable.
- [ ] Les huit questions d'observation sont répondues dans `journal.md`.
- [ ] Le verdict sur `analyze` / `converge` est posé : **gate fiable ou pas ?**
- [ ] Les trois prompts sont lus, l'instruction anti-invention est relevée.

→ Palier suivant : [`04-chantier-conduit.md`](04-chantier-conduit.md)

---

## Sources

- [github/spec-kit — README](https://github.com/github/spec-kit)
- [Documentation Spec Kit](https://github.github.io/spec-kit/)
- [Référence des intégrations](https://github.github.io/spec-kit/reference/integrations.html)
- [Adopter Spec Kit sur un projet existant](https://github.github.io/spec-kit/guides/existing-projects.html)
