---
title: "Palier 3 — SpecKit, la mécanique"
description: "Installation, initialisation, les deux layouts Copilot, les dix commandes et leurs artefacts. Un cycle jetable bouclé de bout en bout avant de toucher à Conduit."
date: 2026-09-05
status: ACTIVE
effort: "~5 h"
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
# uv, le gestionnaire d'outils Python. Installeur officiel Astral :
# binaire préconstruit, posé dans ~/.local/bin (à avoir dans le PATH).
curl -LsSf https://astral.sh/uv/install.sh | sh

# SpecKit, ÉPINGLÉ. Ne pas installer la branche flottante.
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git@v1.0.4

# Vérifier les prérequis et les agents détectés
specify check
```

> **Pourquoi pas `brew install uv`.** La formule Homebrew marche, mais elle n'a pas de bottle
> pour toutes les plateformes. Sur macOS **Intel (x86_64)**, Homebrew retombe sur une
> compilation depuis les sources Rust : il tire `rust`, `llvm`, `cmake`, puis lance un
> `cargo install` de plusieurs dizaines de minutes — pendant lesquelles il tient un verrou sur
> le Cellar, `brew link uv` échoue, et `uv` reste introuvable. Le symptôme observé est
> `zsh: command not found: specify`, alors que la cause est deux étages plus bas. L'installeur
> Astral télécharge le binaire déjà compilé et rend la main en quelques secondes, sur Intel
> comme sur Apple Silicon. C'est aussi la méthode recommandée en amont par Astral.

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
cd ~/IdeaProjects/conduit-speckit
git checkout -b chore/speckit-bootstrap        # la baseline, impérativement

specify init --here --integration copilot
```

`--here` initialise dans le répertoire courant au lieu de créer un sous-dossier. `--force`
existe pour écraser des fichiers en conflit — **c'est pour ça que la branche de référence est
obligatoire**, la doc SpecKit l'indique explicitement.

> **Ce que l'init n'écrase pas — vérifié, pas supposé.** La question se pose légitimement, parce
> que `--force` « remplace des fichiers aux chemins en conflit » et que le
> [palier 1, §1.4](01-socle-copilot.md) t'a fait écrire à la main quatre fichiers de contexte.
> Constat en bac à sable sur v1.0.4, les deux layouts :
>
> | Chemin | Écrit par `specify init` ? |
> |---|---|
> | `.specify/` (templates, scripts, `memory/constitution.md`, workflows) | **oui** |
> | `.github/skills/speckit-*/SKILL.md` | oui — layout *skills* uniquement |
> | `.github/agents/*.agent.md` + `.github/prompts/*.prompt.md` + `.vscode/settings.json` | oui — layout *commands* uniquement |
> | `.github/copilot-instructions.md` | **non** |
> | `.github/instructions/*.instructions.md` | **non** |
> | `AGENTS.md` | **non** |
>
> **Les deux couches sont disjointes.** SpecKit pose des *commandes* ; le palier 1 pose du
> *contexte*. L'init consomme le second sans jamais y toucher. Le socle du palier 1 survit à un
> `specify init --force`, et il survivrait même à une réinitialisation complète.

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

Six forment le cycle principal, quatre sont optionnelles mais font une grande partie de la
valeur réelle.

### Le cycle principal

| Ordre | Commande | Rôle | Produit |
|---|---|---|---|
| 0 | `/speckit-constitution` | Les principes gouvernant le projet | `.specify/memory/constitution.md` |
| 1 | `/speckit-specify` | Le **quoi** et le **pourquoi** | `specs/NNN-slug/spec.md` + une branche |
| 2 | `/speckit-clarify` | Les ambiguïtés qui bloquent une preuve | `spec.md` enrichie |
| 3 | `/speckit-plan` | Le **comment** : stack, architecture, contrats et stratégie de preuve | `plan.md`, `research.md`, `data-model.md`, `contracts/` |
| 4 | `/speckit-tests` | Cas AC et scénarios Cucumber dérivés | `test-cases.yaml`, feature files, `traceability.md` |
| 5 | `/speckit-tasks` | L'**ordre** : unités exécutables | `tasks.md` |
| 6 | `/speckit-implement` | L'exécution et les gates | Le code et les preuves d'exécution |
| 7 | `/speckit-converge` | Confronte le code réel aux artefacts et **rouvre du travail** | Ajouts dans `tasks.md` |

### Les commandes optionnelles — là où se joue la qualité

| Commande | Quand | Pourquoi elle compte |
|---|---|---|
| `/speckit-analyze` | Après `tasks` | Vérifie la cohérence **entre** spec, plan et tasks. Détecte le fonctionnel qui a fui dans le plan. |
| `/speckit-checklist` | À la demande | Génère une checklist de validation qualité. Le support d'un gate outillé. |
| `/speckit-taskstoissues` | Après `tasks` | Convertit `tasks.md` en issues GitHub → cloud agent. Voir [palier 5](05-industrialisation.md). |

> **La commande à ne jamais sauter est `/speckit-clarify`.** Sans elle, l'agent comble les
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
/speckit-constitution

Projet d'entraînement, Java 25 LTS + Spring Boot. Principes :
1. Toute API a un contrat OpenAPI avant son implémentation.
2. Tout endpoint a un test d'intégration qui passe par la couche HTTP réelle.
3. Le domaine ne dépend d'aucune annotation de framework.
```

```
/speckit-specify

Un endpoint de santé qui indique si l'application est prête à servir du trafic,
en distinguant l'application démarrée de ses dépendances joignables.
```

Puis `/speckit-clarify`, `/speckit-plan`, `/speckit-tests`, `/speckit-tasks`,
`/speckit-analyze`, `/speckit-implement`, `/speckit-converge`.

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

## 3.6 — L'équivalent des rules : une architecture en trois couches

La question qui vient systématiquement quand on arrive de `.claude/rules/` : *« où je mets mes
conventions ? »* La réponse par défaut — « dans la constitution » — est fausse, et elle produit
un fichier fourre-tout que le découpage en rules scopées avait justement résolu.

### Pourquoi une seule couche ne suffit pas

Les 21 rules de `conduit-fullstack` font en réalité **deux métiers différents** :

| Métier de la rule | Exemple | Quand elle doit être lue |
|---|---|---|
| Gouverner une **décision** | `12-backend-hexagonal.md` : « le domaine est isolé du framework » | Au moment de l'arbitrage : `plan`, `tasks`, `analyze` |
| Gouverner **l'écriture d'un fichier** | Le même, décliné : « pas de `@Entity` sous `domain/` » | Quand l'agent touche un fichier correspondant, et en revue |

SpecKit sépare ces deux métiers sur deux mécanismes distincts. Ce n'est pas de la duplication :
c'est **deux altitudes**. Une règle d'architecture qui ne vit que dans `.github/instructions/`
n'atteindra jamais `/speckit.plan`, là où la décision se prend. Une règle qui ne vit que dans
la constitution n'aura rien pour l'appliquer ligne à ligne, ni pour la vérifier en revue.

### Couche 1 — Constitution : les décisions

`.specify/memory/constitution.md`. Relue **à chaque exécution** de `plan`, `tasks` et `analyze`.
Le gabarit porte un bloc de gouvernance versionné :

```
**Version**: 2.1.1 | **Ratified**: 2026-09-05 | **Last Amended**: 2026-09-12
```

Peu de principes, non négociables, tous vérifiables. **Aucun mécanisme de scoping** : il n'y a
pas d'`applyTo` dans la constitution.

### Couche 2 — Instructions scopées : les frappes

`.github/instructions/*.instructions.md` avec `applyTo`. C'est la traduction **directe** des
scopes de tes rules, et c'est un mécanisme **Copilot**, pas SpecKit — d'où le fait qu'aucune
documentation SpecKit ne le mentionne. Voir [palier 1](01-socle-copilot.md) et
[`reference/copilot-customisation.md`](reference/copilot-customisation.md).

### Couche 3 — Presets : la réutilisation

Le mécanisme réellement modulaire de SpecKit, et **plus puissant que `.claude/rules/`** sur ce
terrain. Des collections empilables et ordonnées par priorité, résolues à l'exécution :

```
.specify/templates/overrides/       ← surcharges ponctuelles du projet
.specify/presets/<id>/templates/    ← presets installés, triés par priorité
.specify/extensions/<id>/templates/ ← templates fournis par des extensions
.specify/templates/                 ← templates du cœur
```

```bash
specify preset add enterprise-safe --priority 10       # couche de base
specify preset add healthcare-compliance --priority 5  # écrase la précédente
specify preset resolve spec-template                   # dit qui gagne, et pourquoi
```

Priorité basse = l'emporte. Un preset **remplace** par défaut ; des *composition strategies*
(dont `wrap`) permettent d'augmenter au lieu d'écraser.

> **L'argument qui porte en équipe multi-dépôts** : tes 21 rules sont des copies par dépôt, que
> personne ne voit de façon centralisée. Un preset appartient à une équipe socle, est versionné,
> audité en un seul endroit, et se déploie sur N dépôts. C'est l'ambition de `.claude/rules/`,
> mais à l'échelle d'une organisation.

### Le pont vers les fichiers de contexte : l'extension `agent-context`

Point contre-intuitif, à connaître avant de promettre quoi que ce soit :

> *« Spec Kit itself never touches your agent context file. This extension is the only thing
> that does, and it's opt-in. »*

```bash
specify extension add agent-context     # PAS installée par `specify init`
```

Elle gère un bloc délimité par `<!-- SPECKIT START -->` / `<!-- SPECKIT END -->` dans
`CLAUDE.md`, `.github/copilot-instructions.md`, `AGENTS.md`… Tout ce qui est hors du bloc reste
intact. Le champ `context_files` en synchronise plusieurs à la fois — utile ici, où
`CLAUDE.md` et `copilot-instructions.md` coexistent. Hooks `after_specify` et `after_plan`,
plus la commande `speckit.agent-context.update`.

### Le piège : `constitution-sync`

Ce preset propage la constitution dans `plan-template.md`, `spec-template.md`,
`tasks-template.md` et les docs. Ça ressemble à ce qu'on veut — **GitHub le déconseille
explicitement par défaut** :

> *« Propagation was removed deliberately — it duplicates the constitution as the source of
> truth and can fight the composition stack (materialized edits get shadowed or clobbered on
> the next recompose). »*

À n'installer que si l'équipe relit les templates matérialisés **comme des artefacts committés
en PR**. Sinon, la résolution à l'exécution garde la constitution comme source unique : rien à
resynchroniser, donc rien qui dérive.

### Le mapping concret des 21 rules

| Rules | Destination |
|---|---|
| `01`, `02`, `18`, `20`, `21` — tâches, workflow, traçabilité, exigences, cadre | **Constitution**, section Governance |
| `03` — commits & ADR | Scindée : convention de commit → `copilot-instructions.md` · obligation d'ADR → constitution |
| `16` (tests obligatoires), `19` (sécurité by design) | **Constitution** en principe NON-NÉGOCIABLE, + instructions scopées pour le détail |
| `10`, `11`, `13`, `14`, `15`, `17` | **`.github/instructions/`** avec `applyTo` — traduction directe des scopes existants |
| `12` — hexagonal | **Les deux** : le principe en constitution, l'interdiction d'annotations en `applyTo: **/domain/**` |
| `00` — mémoire | Ni l'un ni l'autre → `AGENTS.md` |

### Ce qu'on perd, ce qu'on gagne

**Perdu** : aucun `applyTo` dans la constitution — le scoping natif de SpecKit n'existe pas, il
faut descendre sur le mécanisme Copilot. Et un fichier unique qui grossit dilue ses consignes,
là où 21 fichiers scopés restent nets.

**Gagné** : la constitution est **versionnée** (`Version | Ratified | Last Amended` + Sync
Impact Report), ce que `.claude/rules/` n'a pas ; `/speckit.analyze` la confronte aux artefacts,
ce qui en fait un gate réel ; les presets donnent la réutilisation inter-dépôts.

**Exercice 3.6** (45 min) — Reprendre les 21 rules de `conduit-fullstack` et les répartir sur
les trois couches, en justifiant chaque affectation par le critère *« à quel moment cette règle
doit-elle être lue ? »*. Les cas qui vont dans **deux** couches sont les plus instructifs :
c'est là qu'on voit la différence d'altitude entre un principe et son application. Consigner
dans [`journal.md`](journal.md) — ce tri est un atelier d'équipe directement réutilisable.

---

## Critère de sortie — récapitulatif

- [ ] SpecKit v1.0.4 installé et **épinglé**.
- [ ] Les deux layouts comparés, `--commands` retenu et justifié.
- [ ] Un cycle complet bouclé sur la feature jetable.
- [ ] Les huit questions d'observation sont répondues dans `journal.md`.
- [ ] Le verdict sur `analyze` / `converge` est posé : **gate fiable ou pas ?**
- [ ] Les trois prompts sont lus, l'instruction anti-invention est relevée.
- [ ] Les 21 rules sont réparties sur les trois couches, avec justification.

→ Palier suivant : [`04-chantier-conduit.md`](04-chantier-conduit.md)

---

## Sources

- [github/spec-kit — README](https://github.com/github/spec-kit)
- [Documentation Spec Kit](https://github.github.io/spec-kit/)
- [Référence des intégrations](https://github.github.io/spec-kit/reference/integrations.html)
- [Adopter Spec Kit sur un projet existant](https://github.github.io/spec-kit/guides/existing-projects.html)
- [Presets — stacking, priorité, composition strategies](https://github.com/github/spec-kit/tree/main/presets)
- [Extension `agent-context`](https://github.com/github/spec-kit/tree/main/extensions/agent-context)
- [Preset `constitution-sync` — et ses caveats](https://github.com/github/spec-kit/tree/main/presets/constitution-sync)
- [Gabarit de constitution](https://github.com/github/spec-kit/blob/main/templates/constitution-template.md)
