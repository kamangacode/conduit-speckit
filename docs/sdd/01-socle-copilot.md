---
title: "Palier 1 — Le socle GitHub Copilot"
description: "Les quatre surfaces Copilot, la hiérarchie des fichiers de customisation et quelle surface honore quoi. Le prérequis pour diagnostiquer SpecKit au lieu de le subir."
date: 2026-09-05
status: ACTIVE
effort: "~4 h"
---

# Palier 1 — Le socle GitHub Copilot

> **Objectif** : cesser de traiter Copilot comme une boîte noire à qui l'on parle, commencer à
> le traiter comme un système qu'on configure.
>
> **Critère de sortie** : 3 prédictions correctes d'affilée sur l'effet d'un changement de
> configuration, consignées dans [`journal.md`](journal.md).

---

## Pourquoi ce palier vient en premier

SpecKit ne fait rien de magique. Il pose des fichiers Markdown structurés à des endroits que
Copilot sait lire, et il découpe le travail en étapes. **Tout son pouvoir vient de la façon
dont Copilot consomme du contexte.**

Conséquence directe pour un coach : quand une commande SpecKit rend un résultat médiocre, il
n'y a que trois causes possibles — (1) la spec en entrée est floue, (2) le contexte du dépôt
est absent ou contradictoire, (3) la tâche dépasse la fenêtre de l'agent. Savoir trancher entre
les trois **en direct, devant un développeur** est ce qui sépare un coach d'un animateur de
démo. Et ça demande de connaître la mécanique de contexte, pas les slash commands.

---

## 1.1 — Les quatre surfaces

Copilot n'est pas un produit, c'est quatre produits qui partagent une marque. Ils n'ont ni le
même contexte, ni les mêmes limites, ni les mêmes fichiers de configuration.

| Surface | Où ça tourne | Ce qu'elle voit | Usage en SDD |
|---|---|---|---|
| **IDE (VS Code, mode agent)** | Poste local | Le workspace ouvert, les fichiers joints, les outils MCP | **C'est ici que tournent les commandes SpecKit.** Le cœur du programme. |
| **Cloud agent** | Runner GitHub | Un dépôt, une branche, 59 min max | Exécute une issue → ouvre une PR. Cible de `/speckit.taskstoissues`. |
| **Code review** | PR sur GitHub | Le diff de la PR | Applique les `.instructions.md` ciblés. Le levier le plus sous-estimé. |
| **CLI** | Terminal | Le répertoire courant + ses instructions propres | Tâches hors IDE, scripts, exploration. |

**L'erreur qui coûte le plus cher en équipe** : écrire une consigne dans un fichier que la
surface visée ne lit pas, constater que « Copilot n'écoute pas », et conclure que la
configuration ne sert à rien. À partir de là, l'équipe revient au prompt libre et tout le
bénéfice de SDD disparaît.

**Exercice 1.1** (45 min) — Pour chacune des 4 surfaces, écrire dans
[`journal.md`](journal.md) : à quel moment du cycle SDD elle intervient, et quelle est sa
limite dure. Puis vérifier sur ton tenant lesquelles sont réellement activées (le cloud agent
demande un plan payant **et** une policy activée par un admin d'organisation).

---

## 1.2 — La hiérarchie des fichiers de customisation

C'est le cœur du palier. Trois niveaux, du plus large au plus ciblé.

### Niveau 1 — Instructions de dépôt

```
.github/copilot-instructions.md
```

Markdown libre, pas de frontmatter. S'applique à **toutes** les requêtes faites dans le
contexte du dépôt. Honoré par le chat, le cloud agent et la code review.

C'est l'équivalent direct de ton `CLAUDE.md` de `conduit-fullstack`. Mêmes qualités attendues :
court, impératif, sans redite. Un fichier de 400 lignes qui répète la doc du framework dilue
les consignes qui comptent.

### Niveau 2 — Instructions ciblées par chemin

```
.github/instructions/<nom>.instructions.md
```

Avec un frontmatter **obligatoire** :

```markdown
---
applyTo: "src/main/java/**/domain/**/*.java"
---

Le domaine ne dépend d'aucune annotation Spring. Pas de `@Entity`, pas de `@Service`,
pas d'`@Autowired` sous `domain/`. La persistance vit dans `infrastructure/`.
```

`applyTo` prend des globs. C'est l'analogue exact de tes rules modulaires
`.claude/rules/12-backend-hexagonal.md` avec leur scope `apps/api/**`.

> **Le point à vérifier sur ton tenant, et à ne pas supposer** : la documentation GitHub
> indique que les instructions ciblées par chemin sont honorées par le **cloud agent et la code
> review**, alors que VS Code en a son propre support, plus large. Les deux documentations ne
> décrivent pas le même périmètre. **Ne pas trancher depuis la doc — trancher depuis
> l'observation** (exercice 1.3). Et le noter dans le playbook : c'est exactement le genre de
> détail sur lequel une équipe perd une demi-journée.

### Niveau 3 — Instructions d'agent

```
AGENTS.md          (n'importe où dans le dépôt ; le plus proche l'emporte)
CLAUDE.md          (racine)
GEMINI.md          (racine)
```

`AGENTS.md` est le standard inter-outils. Le fait que Copilot lise aussi `CLAUDE.md` a une
conséquence pratique appréciable pour toi : **un dépôt déjà instrumenté pour Claude Code est
déjà partiellement instrumenté pour Copilot.** À vérifier, et à exploiter si c'est confirmé.

L'imbrication est la fonctionnalité intéressante en monorepo : un `AGENTS.md` à la racine pour
les conventions générales, un autre sous `services/billing/` pour ce qui n'est vrai que là. Le
plus proche du fichier édité l'emporte.

### Les autres mécanismes (surtout côté VS Code)

| Mécanisme | Chemin | À quoi ça sert |
|---|---|---|
| **Prompt files** | `.github/prompts/*.prompt.md` | Un prompt réutilisable, invocable comme une commande |
| **Custom agents** | `.github/agents/*.agent.md` | Un agent spécialisé, invocable par `@nom` |
| **Chat modes** | Ask / Edit / Agent | Trois postures ; SDD vit en mode **Agent** |
| **MCP** | Configuration VS Code | Donne à Copilot des outils externes |

> **Note SpecKit** : ces deux premiers chemins ne sont pas anodins. Le layout *commands* de
> l'intégration Copilot de SpecKit écrit précisément dans `.github/agents/*.agent.md` et
> `.github/prompts/*.prompt.md`. Voir [palier 3](03-speckit.md).

Tableau complet et à jour : [`reference/copilot-customisation.md`](reference/copilot-customisation.md).

---

## 1.3 — L'exercice qui valide le palier

**Le principe** : tu ne valides pas ce palier en sachant *décrire* les fichiers. Tu le valides
en sachant **prédire** leur effet. La prédiction faite *avant* l'observation est la seule
mesure honnête de la compréhension — écrire la prédiction après coup ne prouve rien.

**Protocole** (3 manches, ~1 h 30) :

Pour chaque manche : (a) écrire la prédiction dans `journal.md`, (b) exécuter, (c) noter
l'écart. Trois manches justes d'affilée = palier validé.

| # | Manipulation | Ce qu'il faut prédire |
|---|---|---|
| 1 | Ajouter dans `.github/copilot-instructions.md` : *« Toute réponse commence par le chemin du fichier concerné. »* Puis poser une question dans le chat. | La réponse respecte-t-elle la consigne ? La consigne survit-elle à une deuxième question dans le même fil ? |
| 2 | Créer `.github/instructions/domain.instructions.md` avec `applyTo: "**/domain/**"` interdisant les annotations Spring. Demander à Copilot de créer une entité **sous** `domain/`, puis une **hors** de `domain/`. | La consigne s'applique-t-elle dans les deux cas, dans un seul, ou dans aucun ? **C'est la manche qui tranche la question de périmètre du niveau 2.** |
| 3 | Poser deux consignes **contradictoires** : une dans `copilot-instructions.md`, une dans `AGENTS.md`. Poser une question qui déclenche les deux. | Laquelle gagne ? Copilot signale-t-il le conflit ou en choisit-il une en silence ? |

> La manche 3 est la plus instructive et celle que personne ne fait. En équipe, les consignes
> contradictoires ne sont pas une hypothèse d'école : elles apparaissent mécaniquement dès que
> deux personnes éditent la configuration sans se parler. **Savoir ce que fait Copilot dans ce
> cas est une compétence de coach**, parce que le symptôme observé par le développeur sera
> « Copilot est devenu incohérent », jamais « nos instructions se contredisent ».

---

## 1.4 — Instrumenter `github-speckit` pour de vrai

Une fois les trois manches faites, poser le socle réel du dépôt. Il servira tout le programme.

```
github-speckit/
├── .github/
│   ├── copilot-instructions.md          # conventions générales du dépôt
│   └── instructions/
│       ├── domain.instructions.md       # applyTo: **/domain/**
│       ├── tests.instructions.md        # applyTo: **/src/test/**
│       └── api-contract.instructions.md # applyTo: **/interface/**, **/controller/**
└── AGENTS.md                            # le point d'entrée inter-outils
```

**Ce qu'il faut mettre dans `copilot-instructions.md`, et rien d'autre** :

1. Ce qu'est le projet, en trois lignes, avec un lien vers [le PRD](../prd/PRD-conduit.md).
2. La stack et sa version : Java 21, Spring Boot, Maven ou Gradle — tranche et écris-le.
3. Les conventions non devinables depuis le code : nommage, structure des packages, format des
   commits.
4. **Les invariants du contrat RealWorld** que Copilot ne peut pas deviner et se trompera
   systématiquement à leur sujet :
   - l'en-tête d'authentification est `Authorization: Token <jwt>`, **pas** `Bearer` — c'est
     l'erreur numéro un, parce que tous les corpus d'entraînement disent `Bearer` ;
   - les erreurs de validation renvoient `422` avec `{"errors":{"champ":["message"]}}` ;
   - les endpoints de **liste** d'articles ne renvoient pas le `body` (règle R-7 du PRD) ;
   - `following` et `favorited` valent `false` pour un visiteur anonyme.

Ce quatrième point est le plus important du palier. **Un fichier d'instructions ne vaut que par
ce qu'il contient d'improbable.** Écrire « le code doit être lisible » est du bruit : c'est
déjà le comportement par défaut. Écrire « le préfixe est `Token`, pas `Bearer` » corrige un
biais réel, mesurable, répétitif. C'est le critère à transmettre à l'équipe : *une instruction
qui ne corrige aucun penchant par défaut n'a rien à faire dans le fichier.*

**Exercice 1.4** (45 min) — Écrire les 4 fichiers. Puis mesurer : demander à Copilot un
contrôleur d'authentification **avant** et **après** avoir posé les instructions, et comparer
les deux sorties sur le seul critère `Token` vs `Bearer`. Coller les deux extraits dans
`journal.md`.

---

## Critère de sortie — récapitulatif

- [ ] Les 4 surfaces sont décrites dans `journal.md`, avec leur limite dure et leur rôle SDD.
- [ ] Les 3 manches de prédiction sont passées, avec l'écart noté pour chacune.
- [ ] La question de périmètre du niveau 2 (`applyTo`) est **tranchée par l'observation**, pas
      par la doc, et la réponse est écrite.
- [ ] `github-speckit` a ses 4 fichiers de contexte.
- [ ] La preuve avant/après sur `Token` vs `Bearer` est dans `journal.md`.

→ Palier suivant : [`02-methode-sdd.md`](02-methode-sdd.md)

---

## Sources

- [Ajouter des instructions de dépôt pour Copilot — GitHub Docs](https://docs.github.com/en/copilot/how-tos/configure-custom-instructions/add-repository-instructions)
- [Le cloud agent supporte AGENTS.md — GitHub Changelog](https://github.blog/changelog/2025-08-28-copilot-coding-agent-now-supports-agents-md-custom-instructions/)
- [À propos du cloud agent — GitHub Docs](https://docs.github.com/en/copilot/concepts/agents/coding-agent/about-coding-agent)
- [Personnalisation de Copilot dans VS Code](https://code.visualstudio.com/docs/copilot/customization/overview)
- [github/awesome-copilot — exemples d'instructions](https://github.com/github/awesome-copilot)
