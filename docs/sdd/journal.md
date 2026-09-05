---
title: "Journal de bord — programme SDD"
description: "Le registre daté des observations, mesures et écarts constatés pendant le programme. Seule source légitime des anti-patterns du playbook."
date: 2026-09-05
status: ACTIVE
---

# Journal de bord

> **À remplir en continu, pas à la fin.** Une observation notée trois jours plus tard est
> reconstruite, donc lissée : on se souvient de la conclusion, plus du symptôme qui y a mené.
> Or c'est le symptôme qui sert au coaching, parce que c'est lui que l'équipe rencontrera.

---

## Pourquoi ce fichier existe

Le [palier 6](06-kit-coaching.md) impose que chaque affirmation du playbook renvoie à une
entrée datée d'ici. Sans ce journal, le kit de coaching se remplit de bonnes pratiques
recopiées — et un coach qui récite se fait démonter à la première objection de terrain.

**Le format d'une entrée utile** :

```markdown
### AAAA-MM-JJ — Titre court du constat

**Contexte** : où, quelle étape, quelle commande.
**Symptôme** : ce qui a été observé, littéralement. Coller la sortie.
**Cause** : spec / plan / constitution / agent / outil.
**Correction** : ce qui a été changé, et à quel niveau.
**À retenir pour le coaching** : la formulation transmissible, ou « rien » si l'incident est
anecdotique.
```

Le champ **Cause** est celui qui compte. C'est sa distribution sur l'ensemble du programme qui
produit le chiffre le plus utile du kit : *quelle part des défauts vient de l'intention, et
quelle part vient de l'agent ?*

---

## Palier 1 — Socle Copilot

### Les 4 surfaces — relevé

| Surface | Activée sur mon tenant ? | Limite dure | Rôle dans le cycle SDD |
|---|---|---|---|
| IDE (VS Code, mode agent) | | | |
| Cloud agent | | | |
| Code review | | | |
| CLI | | | |

### Les 3 manches de prédiction

| # | Manipulation | Prédiction (écrite **avant**) | Observé | Écart |
|---|---|---|---|---|
| 1 | Consigne de format dans `copilot-instructions.md` | | | |
| 2 | `applyTo` sur `**/domain/**`, dedans puis dehors | | | |
| 3 | Consignes contradictoires `copilot-instructions.md` vs `AGENTS.md` | | | |

**Verdict sur le périmètre de `.instructions.md`** *(tranché par observation, pas par la doc)* :

> _à remplir_

### Preuve avant / après — `Token` vs `Bearer`

**Avant instructions** :
```java
// coller l'extrait
```

**Après instructions** :
```java
// coller l'extrait
```

---

## Palier 2 — Méthode SDD

### Tri des règles R-1 à R-10 (PRD §11)

| Règle | spec ou plan ? | Justification en une ligne |
|---|---|---|
| R-1 (slug depuis le titre, kebab-case) | | *cas à argumenter des deux côtés avant de trancher* |
| R-2 (tri par date décroissante) | | |
| R-3 (filtres cohérents) | | |
| R-4 (feed = auteurs suivis, auth requise) | | |
| R-5 (`following`/`favorited` relatifs) | | |
| R-6 (seul l'auteur édite/supprime → 403) | | |
| R-7 (pas de `body` dans les listes) | | |
| R-8 (email et username uniques) | | |
| R-9 (password hashé, jamais renvoyé) | | |
| R-10 (pagination : limit 20, offset 0) | | |

### Mapping SpecKit ↔ pipeline maison — colonnes comparatives

| Étape | SpecKit fait mieux | Le dispositif maison fait mieux |
|---|---|---|
| Constitution / rules | | |
| Spec | | |
| Plan | | |
| Tasks | | |
| Review / validate | | |
| Gates | | |

### La synthèse en 5 minutes

- [ ] Dite à voix haute, chronométrée. Durée réelle : _____

---

## Palier 3 — SpecKit

### Comparaison des deux layouts

| | Layout *skills* | Layout *commands* |
|---|---|---|
| Fichiers posés | | |
| Lisibilité des prompts | | |
| Retenu ? | | |

### Le cycle jetable — les 8 observations

| Étape | Observation |
|---|---|
| `constitution` — invente-t-il des principes non donnés ? | |
| `specify` — du plan a-t-il fui dans la spec ? | |
| `clarify` — combien de questions ? lesquelles étaient décisives ? | |
| `plan` — les choix sont-ils justifiés ou seulement affirmés ? | |
| `tasks` — durée estimée de la plus grosse tâche ? | |
| `analyze` — vraies incohérences ou rapport de complaisance ? | |
| `implement` — la constitution est-elle respectée ? | |
| `converge` — rouvre-t-il du travail réel ? | |

**Verdict — `analyze` et `converge` sont-ils des gates fiables ?**

> _à remplir_

**L'instruction anti-invention relevée dans `speckit.specify.prompt.md`** :

> _citation exacte_

---

## Palier 4 — Chantier Conduit

### Constitution — principes supprimés au filtre des 3 critères

| Principe écarté | Critère non satisfait | Pourquoi |
|---|---|---|

### Tableau de synthèse des trois itérations

| Mesure | It. 1 auth | It. 2 CRUD | It. 3-A (sous-spécifiée) | It. 3-B (tranchée) |
|---|---|---|---|---|
| Échecs Hurl au 1er passage | | | | |
| Cycles de régénération jusqu'au vert | | | | |
| Défauts dus à la **spec** | | | | |
| Défauts dus au **plan** | | | | |
| Défauts dus à la **constitution** | | | | |
| Défauts dus à l'**agent** | | | | |
| Questions posées par `/clarify` | | | | |
| Vrais problèmes trouvés par `/analyze` | | | | |

### Les trois questions de synthèse

**1. Quelle part des défauts venait de l'intention (spec + plan + constitution) plutôt que de
l'agent ?**

> _à remplir — publier le chiffre tel qu'il sort, même s'il déplaît_

**2. `/analyze` et `/converge` sont-ils des gates fiables, ou faut-il un juge externe ?**

> _à remplir_

**3. Quelles corrections portées dans la constitution ont profité aux itérations suivantes ?**

> _à remplir — c'est la démonstration du retour sur investissement du contexte_

### Le chiffre-clé de l'itération 3

**Passe A (sous-spécifiée)** : ____ échecs Hurl
**Passe B (six questions tranchées)** : ____ échecs Hurl

> C'est l'argument central du coaching contre « on perd du temps à écrire des specs ».
> Même code, même juge, deux niveaux de précision d'intention.

---

## Palier 5 — Industrialisation

### Copilot code review — taux de détection

| Violation plantée délibérément | Détectée ? | Reformulation nécessaire ? |
|---|---|---|
| `Bearer` au lieu de `Token` | | |
| Erreur de validation hors 422 / format `errors` | | |
| `body` renvoyé dans une liste d'articles | | |

### Cloud agent — seuil de fiabilité

| Issue | Taille estimée | Résultat | Durée | Contexte du dépôt consommé ? |
|---|---|---|---|---|
| Petite | | | | |
| Moyenne | | | | |
| Trop grosse (délibérément) | | | | |

**Seuil au-delà duquel le cloud agent devient peu fiable sur ce dépôt** : _____

> Donnée locale, dépendante de la stack. Aucune documentation ne peut la fournir — c'est
> exactement ce qu'une équipe attend de son coach.

### Chaîne complète — chronométrage

| Étape | Durée |
|---|---|
| `/speckit.specify` | |
| `/speckit.clarify` + réponses | |
| Revue humaine de la spec (gate) | |
| `/speckit.plan` | |
| `/speckit.tasks` | |
| `/speckit.taskstoissues` | |
| Cloud agent → PR | |
| Copilot code review | |
| CI de conformité | |
| Revue humaine finale | |
| **Total** | |

---

## Palier 6 — Kit de coaching

### Test du playbook sur un tiers

**Cobaye** : _____ · **Date** : _____ · **Atelier rejoué** : 1

| Moment où il a dû demander de l'aide | Trou du playbook | Comblé ? |
|---|---|---|

---

## Écarts constatés avec la documentation

> SpecKit v1.0.4 bouge vite. Chaque écart entre la doc de `docs/sdd/` et le comportement réel
> se note ici **et** se corrige dans le fichier concerné, dans la même session.

| Date | Fichier concerné | Ce qui était écrit | Ce qui a été observé | Corrigé ? |
|---|---|---|---|---|
| 2026-09-05 | `03-speckit.md` §3.1 | `brew install uv` | Sur macOS Intel (x86_64), pas de bottle pour uv 0.12.10 : Homebrew compile depuis les sources Rust, tient le verrou du Cellar pendant des dizaines de minutes, `brew link uv` échoue et `uv` reste introuvable. Symptôme final : `zsh: command not found: specify`. | oui — passage à l'installeur Astral |
| 2026-09-05 | `README.md` étape 3 | `specify integration list \| grep -i copilot` | La commande exige un `.specify/` déjà présent : `Error: Not a Spec Kit project`. Impossible avant init — or l'étape s'intitule « avant de le poser ». Le `grep` masquait l'erreur, d'où une sortie vide inexplicable. | oui — remplacée par `specify check` |
| 2026-09-05 | `README.md` + `01-socle-copilot.md` | Rien entre l'install du CLI et l'exercice 1.1 | Aucune vérification que Copilot répond, ni mention de l'IDE requis. Poste réel : Copilot présent dans JetBrains, pas dans VS Code — alors que tous les mécanismes du programme (`prompts/`, `agents/`, `skills/`, `.vscode/settings.json`) sont VS Code. | oui — nouveau §1.0 |
| 2026-09-05 | `01-socle-copilot.md` §1.0 | *(hypothèse initiale : extension Copilot à installer)* | Faux. Depuis VS Code 1.136, `github.copilot-chat` 0.64.0 est **built-in**. `code --list-extensions` ne le montre pas (faux négatif) et installer `GitHub.copilot` échoue par `cannot be downgraded to version 0.48.1`. | oui — noté comme piège dans le §1.0 |

---

## Anti-patterns observés

> Alimente [`reference/anti-patterns.md`](reference/anti-patterns.md) et le playbook.
> **Uniquement ce qui a réellement été rencontré, avec sa date.**

### AAAA-MM-JJ — _titre_

**Contexte** :
**Symptôme** :
**Cause** :
**Correction** :
**À retenir pour le coaching** :
