---
title: "Référence — customisation de GitHub Copilot"
description: "Quel fichier, à quel chemin, avec quel format, et surtout quelle surface l'honore. Le tableau qui évite à une équipe de perdre une demi-journée."
date: 2026-09-05
status: ACTIVE
verified_against: "GitHub Docs + VS Code Docs, consultés le 2026-09-05"
---

# Référence — customisation de GitHub Copilot

> **L'erreur qui coûte le plus cher en équipe** : écrire une consigne dans un fichier que la
> surface visée ne lit pas, conclure que « Copilot n'écoute pas », et revenir au prompt libre.
> Ce tableau existe pour éviter exactement ça.

---

## Les quatre surfaces

| Surface | Où ça tourne | Contexte visible | Limite dure |
|---|---|---|---|
| **IDE — VS Code, mode agent** | Poste local | Workspace ouvert, fichiers joints, outils MCP | Fenêtre de contexte du modèle |
| **Cloud agent** | Runner GitHub | Un dépôt, une branche | **59 min**, non extensible |
| **Code review** | PR sur GitHub | Le diff de la PR | Ne voit que le diff |
| **CLI** | Terminal | Répertoire courant + ses instructions | — |

**Prérequis d'activation** : le cloud agent demande un plan Copilot payant **et**, en
organisation Business ou Enterprise, une policy activée par un administrateur. Le blocage le
plus fréquent est administratif, pas technique — le lever avant toute démonstration.

---

## Les fichiers de contexte

### 1. Instructions de dépôt

```
.github/copilot-instructions.md
```

- **Format** : Markdown libre, pas de frontmatter.
- **Portée** : toutes les requêtes faites dans le contexte du dépôt.
- **Honoré par** : chat, cloud agent, code review.

### 2. Instructions ciblées par chemin

```
.github/instructions/<nom>.instructions.md
```

- **Format** : frontmatter **obligatoire**.

```markdown
---
applyTo: "src/main/java/**/domain/**/*.java"
---

Consignes applicables uniquement aux fichiers correspondant au glob.
```

- **Champs** : `applyTo` (globs) et, en option, `excludeAgent`.
- **Honoré par** : d'après la documentation GitHub, **cloud agent et code review**. VS Code
  documente un support plus large de son côté.
- **Combinaison** : s'ajoute aux instructions de dépôt quand les deux s'appliquent.

> ⚠️ **Les deux documentations ne décrivent pas le même périmètre.** Ne pas trancher depuis la
> doc — trancher depuis l'observation (manche 2 de l'exercice 1.3). Puis **écrire le résultat
> constaté ici**, daté, parce que c'est le genre de détail sur lequel une équipe perd une
> demi-journée.
>
> Résultat observé sur ce tenant : _à remplir au palier 1_

### 3. Instructions d'agent

```
AGENTS.md      n'importe où dans le dépôt — le plus proche l'emporte
CLAUDE.md      racine
GEMINI.md      racine
```

- **Format** : Markdown libre.
- **Portée** : les agents. `AGENTS.md` est le standard inter-outils.
- **Imbrication** : un `AGENTS.md` racine pour les conventions générales, un autre sous
  `services/facturation/` pour ce qui n'est vrai que là. Le plus proche du fichier édité gagne.
- **Inclusion** : `@chemin/relatif` inclut un autre fichier, y compris en cascade.

> **Conséquence pratique** : Copilot lisant aussi `CLAUDE.md`, un dépôt déjà instrumenté pour
> Claude Code est **déjà partiellement instrumenté pour Copilot**. À vérifier sur son tenant,
> et à exploiter — c'est un raccourci d'adoption réel pour une équipe qui utilise les deux.

### 4. Prompt files, agents personnalisés, chat modes

Surtout côté VS Code :

| Mécanisme | Chemin | Invocation |
|---|---|---|
| **Prompt files** | `.github/prompts/*.prompt.md` | Comme une commande dans le chat |
| **Agents personnalisés** | `.github/agents/*.agent.md` | `@nom` dans le chat |
| **Chat modes** | Ask / Edit / Agent | Sélecteur du chat. **SDD vit en mode Agent.** |
| **MCP** | Configuration VS Code | Outils externes offerts à l'agent |

> **Lien avec SpecKit** : le layout *commands* de l'intégration Copilot écrit précisément dans
> `.github/agents/` et `.github/prompts/`. Les commandes `/speckit.*` ne sont donc rien d'autre
> que des prompt files et des agents personnalisés — c'est la démystification centrale du
> [palier 3](../03-speckit.md).

---

## Tableau récapitulatif — qui honore quoi

| Fichier | Chat IDE | Cloud agent | Code review | CLI |
|---|---|---|---|---|
| `.github/copilot-instructions.md` | ✅ | ✅ | ✅ | ✅ |
| `.github/instructions/*.instructions.md` | *support VS Code propre* | ✅ | ✅ | ? |
| `AGENTS.md` | ✅ | ✅ | ? | ✅ |
| `CLAUDE.md` / `GEMINI.md` | ✅ | ✅ | ? | ✅ |
| `.github/prompts/*.prompt.md` | ✅ | — | — | ? |
| `.github/agents/*.agent.md` | ✅ | — | — | ? |

**Les `?` sont volontaires** : ce sont les cases que la documentation ne tranche pas clairement
et qu'il faut vérifier sur son propre tenant. Un tableau de référence qui affirmerait ce qu'il
ne sait pas serait pire qu'un tableau avec des trous — il ferait perdre du temps à ceux qui s'y
fient. **Remplir les `?` au palier 1, avec la date de vérification.**

---

## Ce qui fait une bonne instruction

> **Une instruction ne vaut que par ce qu'elle a d'improbable.**

| ❌ Bruit | ✅ Utile | Pourquoi |
|---|---|---|
| « Le code doit être lisible » | « L'en-tête d'auth est `Token <jwt>`, pas `Bearer` » | Le premier est déjà le comportement par défaut. Le second corrige un biais réel et répétitif. |
| « Écrire des tests » | « Tout endpoint a un test qui traverse la couche HTTP réelle » | Vérifiable ; on peut refuser une PR qui l'enfreint. |
| « Suivre les bonnes pratiques » | « Aucune annotation Spring sous `domain/` » | Nommable, localisable, testable. |

**Le filtre à trois critères** — une instruction mérite sa place si :

1. elle est **vérifiable** (on peut dire si une PR la respecte) ;
2. elle **corrige un penchant par défaut** de l'agent ;
3. on est **prêt à refuser du code** qui l'enfreint.

Le troisième est le plus sévère, et c'est celui qu'on oublie. Une règle qu'on ne fera jamais
respecter dégrade toutes les autres : elle apprend au lecteur — humain ou agent — que cette
liste est indicative.

---

## Sources

- [Instructions de dépôt — GitHub Docs](https://docs.github.com/en/copilot/how-tos/configure-custom-instructions/add-repository-instructions)
- [Le cloud agent supporte AGENTS.md — Changelog](https://github.blog/changelog/2025-08-28-copilot-coding-agent-now-supports-agents-md-custom-instructions/)
- [À propos du cloud agent — GitHub Docs](https://docs.github.com/en/copilot/concepts/agents/coding-agent/about-coding-agent)
- [Personnalisation de Copilot — VS Code](https://code.visualstudio.com/docs/copilot/customization/overview)
- [github/awesome-copilot](https://github.com/github/awesome-copilot) — exemples d'instructions
