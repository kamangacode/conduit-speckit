---
title: "Distribuer des instructions communes entre équipes"
description: "Presets, extensions et stratégies de gouvernance pour partager les règles Copilot et Spec Kit à l'échelle d'une organisation."
date: 2026-09-06
status: ACTIVE
---

# Distribuer des instructions communes entre équipes

## Le problème à résoudre

Dans `conduit-fullstack`, les rules sont versionnées dans un dépôt et lues selon leur
périmètre. Dans une entreprise, le besoin est plus large : plusieurs équipes doivent
recevoir un socle commun, l'adapter localement sans le contredire, et pouvoir faire évoluer
le socle sans copier-coller silencieux.

La première décision est de ne pas confondre trois objets :

| Objet | Question à laquelle il répond | Mécanisme naturel |
|---|---|---|
| Principe non négociable | Quelle décision doit être vraie pour tous les projets ? | Constitution ou policy |
| Instruction de travail | Que doit faire Copilot quand il touche certains fichiers ? | `.github/instructions/*.instructions.md` |
| Composant Spec Kit | Quels templates et workflows doivent être installés ? | Preset ou extension |

Un preset n'est donc pas automatiquement le remplacement d'un dossier de rules. Il est le
mécanisme de réutilisation de Spec Kit pour ses propres composants. La distribution des
instructions Copilot demande une couche complémentaire.

## Les trois couches dans une organisation

### 1. La constitution du projet

`.specify/memory/constitution.md` contient peu de principes, stables et vérifiables :
spécification avant implémentation, traçabilité, sécurité, tests, gouvernance et règles de
versioning.

Elle est propre au projet, même si un preset peut fournir son template initial. Elle ne doit
pas devenir un catalogue de règles détaillées par langage ou par dossier : elle n'a pas de
`applyTo` et sa lecture intervient au moment où Spec Kit produit ou analyse les artefacts.

**À distribuer par cette couche :** les décisions qui doivent influencer `plan`, `tasks`,
`analyze` et `converge`.

**À ne pas y distribuer :** les détails comme « ne pas mettre `@Entity` sous `domain/` » ou
les conventions CSS d'une application.

### 2. Les instructions Copilot scopées

Les fichiers `.github/instructions/*.instructions.md` sont la traduction directe des rules
scopées :

```text
.github/instructions/
├── clean-code.instructions.md
├── hexagonal-architecture.instructions.md
├── api-contract.instructions.md
├── tests.instructions.md
└── documentation.instructions.md
```

Chaque fichier porte un frontmatter :

```markdown
---
applyTo: "**/domain/**/*.java, **/application/**/*.java"
---

Le domaine ne dépend d'aucune annotation Spring ou JPA.
```

Cette couche est l'endroit naturel des règles de frappe : elle est chargée quand Copilot
travaille dans le périmètre correspondant et peut également être utilisée par la revue de
code selon la surface activée.

**À distribuer par cette couche :** clean code, architecture, contrats API, tests, sécurité
et conventions documentaires liées à un chemin.

**Limite :** Spec Kit ne résout pas ces fichiers comme des templates. Un preset Spec Kit ne
les installe pas nécessairement dans tous les dépôts. Il faut donc les livrer avec le projet,
une extension, un dépôt de bootstrap ou une automation d'organisation.

### 3. Les presets et extensions Spec Kit

Les presets et extensions servent à réutiliser le comportement Spec Kit entre dépôts.
Dans la version locale vérifiée ici (`specify 1.0.4`) :

```bash
specify preset list
specify preset search
specify preset add <id> --priority <n>
specify preset resolve spec-template
specify preset set-priority <id> <n>
specify preset enable <id>
specify preset disable <id>

specify extension list
specify extension search
specify extension add <id>
specify extension info <id>
specify extension update <id>
```

La pile de résolution des templates est conceptuellement :

```text
.specify/templates/overrides/       # exception locale
.specify/presets/<id>/templates/    # presets installés
.specify/extensions/<id>/templates/ # templates fournis par extension
.specify/templates/                 # templates du projet / du coeur
```

La priorité et la stratégie de composition déterminent quel template est retenu. Il faut
lire la sortie de `specify preset resolve <template>` avant de conclure qu'un template est
actif. Une résolution à l'exécution évite de matérialiser le même texte dans tous les
projets, mais elle rend la version du CLI, du preset et du catalogue importante.

### Ce qu'un preset peut distribuer

Un preset est pertinent pour un socle qui doit modifier ou compléter les artefacts Spec Kit :

- `spec-template.md` avec des sections d'exigences propres au domaine ;
- `plan-template.md` avec une analyse de sécurité ou de performance obligatoire ;
- `tasks-template.md` avec des tâches de migration, test ou documentation ;
- des stratégies de composition et des métadonnées du preset ;
- selon le preset ou l'extension, des commandes ou hooks intégrés au workflow.

### Ce qu'un preset ne garantit pas

Un preset ne garantit pas à lui seul la distribution de fichiers arbitraires comme :

- `.github/copilot-instructions.md` ;
- `.github/instructions/*.instructions.md` ;
- une policy GitHub d'organisation ;
- une règle de protection de branche ;
- une action CI déjà présente dans chaque dépôt.

Pour ces éléments, utiliser une extension explicitement conçue pour les installer, ou une
stratégie de dépôt/automation séparée. Ne pas annoncer « le preset distribue les rules »
sans vérifier les fichiers réellement installés et leur manifest.

## Quatre modèles de distribution

### Modèle A — Dépôt de bootstrap ou template repository

Créer un dépôt de référence contenant :

```text
org-copilot-baseline/
├── .github/copilot-instructions.md
├── .github/instructions/
├── AGENTS.md
├── .specify/templates/
├── .specify/workflows/
└── scripts/verify-agent-baseline.sh
```

Un nouveau projet part de ce dépôt ou exécute une commande d'initialisation qui copie ces
fichiers. C'est le modèle le plus simple pour commencer.

**Avantages :** transparent, facile à auditer, compatible avec tous les fichiers Copilot.

**Risques :** les projets déjà créés dérivent si aucune synchronisation n'existe ; une copie
n'est pas une dépendance versionnée.

**Quand l'utiliser :** bootstrap de nouveaux dépôts et pilote avec peu d'équipes.

### Modèle B — Package ou release de règles

Mettre les instructions dans un dépôt central versionné et publier une archive ou un package
interne : `org-copilot-baseline@1.3.0`. Un script d'installation installe ou met à jour les
fichiers et écrit la version appliquée dans un manifest :

```json
{
  "baseline": "org-copilot-baseline",
  "version": "1.3.0",
  "files": [
    ".github/copilot-instructions.md",
    ".github/instructions/clean-code.instructions.md"
  ]
}
```

Le script doit préserver les zones locales ou refuser un écrasement lorsque le fichier a été
modifié. Une mise à jour se fait par pull request, jamais par écriture silencieuse sur la
branche de l'équipe.

**Avantages :** versions explicites, changelog, rollback et adoption progressive.

**Risques :** il faut gérer les conflits et le cycle de support des versions.

**Quand l'utiliser :** organisation moyenne ou grande avec plusieurs stacks.

### Modèle C — Preset/extension Spec Kit interne

Publier un preset ou une extension dans un catalogue interne Spec Kit. Le preset porte les
templates et le workflow SDD communs ; l'extension peut fournir des hooks, commandes et
fichiers additionnels si son contrat le permet.

Exemple d'adoption :

```bash
specify preset add org-sdd-core --priority 20
specify extension add org-copilot-context
specify preset resolve spec-template
```

Il faut tester l'installation dans un dépôt jetable et inspecter le manifest avant de le
proposer aux équipes. Épingler la version du catalogue ou du package dans les environnements
reproductibles.

**Avantages :** aligné avec Spec Kit, templates composables, évolution centralisée.

**Risques :** un preset ne suffit pas pour les instructions Copilot arbitraires ; les règles
de priorité et de composition peuvent surprendre ; le CLI et les catalogues doivent rester
compatibles.

**Quand l'utiliser :** quand l'organisation standardise réellement le cycle Spec Kit et ses
artefacts, pas seulement des conventions de code.

### Modèle D — Policy GitHub d'organisation + fichiers de dépôt

Utiliser les mécanismes GitHub d'organisation pour les contrôles qui doivent être globaux :
policies Copilot, règles de contribution, actions obligatoires, secret scanning, CODEOWNERS,
branch protections et règlesets. Garder dans chaque dépôt les instructions Copilot qui
nécessitent un contexte de chemin ou de domaine.

**Avantages :** enforcement centralisé, difficile à contourner par oubli local.

**Risques :** une policy d'organisation ne remplace pas une instruction détaillée de domaine ;
les équipes peuvent ne pas voir pourquoi une règle s'applique ; les capacités dépendent du plan
GitHub et des droits d'administration.

**Quand l'utiliser :** pour les exigences de sécurité, licence, CI et conformité qui sont
réellement communes à toute l'organisation.

## Architecture recommandée

Pour une entreprise multi-équipes, combiner les modèles plutôt que chercher un fichier unique :

```text
Organisation
├── GitHub policy / ruleset              # enforcement global
├── org-copilot-baseline                 # copilot-instructions + instructions scopées
├── org-sdd-core preset                  # templates et gates Spec Kit communs
├── org-conduit-java preset              # contrats et templates Java/Spring/RealWorld
└── équipes
    ├── dépôt A : baseline v1.3 + preset core v2.1 + exceptions locales
    ├── dépôt B : baseline v1.3 + preset core v2.1 + preset frontend
    └── dépôt C : baseline v1.2 + preset core v2.0 pendant migration
```

La séparation recommandée est :

| Niveau | Contenu | Propriétaire |
|---|---|---|
| Organisation | sécurité, secrets, CI minimale, branches, licences | plateforme/sécurité |
| Socle Copilot | langage des règles, revue, documentation, instructions générales | équipe developer experience |
| Preset Spec Kit | templates, artefacts, gates et workflow SDD | équipe SDD/architecture |
| Preset métier | contrat Conduit, Java/Spring, RealWorld | équipe produit/domaine |
| Dépôt | constitution, `applyTo` local, exceptions justifiées | équipe propriétaire |

## Règles de gouvernance

### Versionner et épingler

Chaque dépôt doit déclarer la version de son socle et de ses presets. Une équipe ne doit pas
recevoir une modification différente selon le jour où elle lance `specify`.

### Préserver l'autonomie locale

Le socle commun définit les invariants. Une équipe peut ajouter une règle plus stricte ou une
exception documentée, mais elle ne doit pas contredire silencieusement une règle de sécurité,
de contrat ou de conformité.

### Faire évoluer par pull request

Une mise à jour du socle doit fournir :

1. un changelog de la règle modifiée ;
2. l'impact sur les templates et commandes ;
3. les migrations nécessaires pour les dépôts existants ;
4. une preuve sur un dépôt pilote ;
5. une période de coexistence si la règle est breaking.

### Vérifier la dérive

Un contrôle non bloquant peut commencer par comparer les checksums des instructions attendues
avec les fichiers du dépôt. Après calibration, il peut devenir un gate CI. Le contrôle doit
signaler les différences locales ; il ne doit pas écraser les fichiers.

### Mesurer l'efficacité

Une règle commune vaut si elle évite une erreur observable. Suivre les écarts : violations en
revue, faux positifs, overrides locaux, temps d'adoption et incidents évités. Retirer une règle
qui ajoute du bruit sans améliorer une décision.

## Plan d'adoption en six étapes

1. **Inventorier** les rules existantes et les classer : constitution, instruction scopée,
   preset, policy ou documentation.
2. **Créer le socle** `org-copilot-baseline` avec 5 à 10 règles vraiment communes.
3. **Créer un preset Spec Kit** pour les templates et le workflow, sans y mettre les fichiers
   Copilot par simple supposition.
4. **Piloter sur deux équipes** avec des stacks différentes et une version épinglée.
5. **Automatiser le contrôle de dérive** en rapport non bloquant, puis calibrer les exceptions.
6. **Passer en enforcement** uniquement pour les règles dont le bruit est connu et le bénéfice
   mesuré.

## Anti-patterns à éviter

- Mettre toutes les rules dans la constitution.
- Copier des instructions dans chaque dépôt sans version ni propriétaire.
- Utiliser un preset pour distribuer des fichiers qu'il ne contient pas dans son manifest.
- Mettre des détails Java dans une règle chargée sur tous les fichiers de l'organisation.
- Écraser automatiquement les personnalisations locales.
- Installer `constitution-sync` uniquement pour éviter de relire la constitution ; cela crée une
  seconde copie qui peut dériver.
- Activer un gate avant d'avoir mesuré ses faux positifs.

## Commandes de diagnostic

Depuis un dépôt cible :

```bash
specify --version
specify preset list
specify extension list
specify preset resolve spec-template
specify check

# Vérifier les fichiers réellement présents
find .github -maxdepth 3 -type f | sort
find .specify -maxdepth 4 -type f | sort
```

Pour ce dépôt, la vérification du 2026-09-06 donne `specify 1.0.4`, l'intégration Copilot
installée et le layout `.github/skills/`. Les prompts `.github/prompts/` ne sont donc pas une
preuve attendue de bonne installation.

## Décision pratique pour Conduit Spec Kit

Pour commencer, conserver cette combinaison :

- `.github/copilot-instructions.md` pour les invariants Conduit et la stack pédagogique ;
- `.github/instructions/` pour les règles scopées déjà créées ;
- `.specify/memory/constitution.md` pour les principes du cycle SDD ;
- un futur preset `org-sdd-core` pour les templates communs entre dépôts ;
- un dépôt ou package de baseline pour distribuer les fichiers Copilot ;
- une vérification CI de dérive avant de rendre le contrôle bloquant.

Cette architecture évite de demander à un seul mécanisme de faire quatre métiers différents :
expliquer le contexte, gouverner les décisions, guider l'écriture d'un fichier et distribuer la
configuration à une organisation.
