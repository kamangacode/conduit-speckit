---
title: "Comparatif SDD - /dev, SpecKit, BMad et OpenSpec"
description: "Équivalence des cycles de travail, forces, limites et artefacts produits par quatre approches SDD appliquées à Conduit."
date: 2026-09-06
status: DRAFT
audience: "Coach technique, développeur qui compare les frameworks SDD"
---

# Comparatif SDD - /dev, SpecKit, BMad et OpenSpec

## Périmètre

Ce document compare quatre manières de piloter la même implémentation Conduit :

- le workflow personnel `/dev` de `conduit-fullstack` ;
- GitHub Spec Kit dans ce dépôt ;
- BMad Method dans `conduit-bmad` ;
- OpenSpec dans `conduit-openspec`.

La comparaison porte sur le cycle de construction, les artefacts produits, les gates humains,
les preuves attendues et la facilité à guider Copilot. Elle ne juge pas encore la qualité du
code généré : ce verdict doit venir après exécution des trois chantiers sur le même périmètre
Conduit.

## Lecture rapide

| Solution | Impression générale | Force principale | Risque principal |
|---|---|---|---|
| `/dev` personnel | Pipeline très contrôlé, adapté au repo et aux habitudes de livraison | Gates explicites, reprise par artefacts, CI/review/ship intégrés | Moins portable : beaucoup de valeur vit dans les règles propres au repo |
| SpecKit | Cycle le plus complet et le plus prescriptif | Commandes spécialisées, artefacts standardisés, clarification et convergence explicites | Peut donner une impression de mécanique lourde si la feature est petite |
| BMad | Plus flexible, orienté story/build | Bon pour dialoguer avec l'agent et ajuster la profondeur selon le besoin | Moins de prompts imposés : il faut ajouter soi-même les gates et contrôles |
| OpenSpec | Très bon modèle de changement versionné | Séparation claire entre specs vivantes, change, deltas, design, tasks et archive | Le cycle dépend beaucoup de la discipline humaine avant `apply` |

L'intuition initiale est donc plutôt juste : SpecKit est plus fourni comme framework de prompts
et de cycle. BMad et OpenSpec sont moins prescriptifs, mais ce n'est pas seulement une faiblesse :
ils laissent plus de marge au contexte du projet. La question devient : veut-on un rail complet,
ou un cadre plus souple que l'on renforce avec ses propres règles ?

## Table d'équivalence des cycles

| Intention SDD | `/dev` personnel | SpecKit | BMad | OpenSpec |
|---|---|---|---|---|
| Cadrer la demande | `/dev #N` puis `triage` et `frame` | `/speckit-specify` démarre la feature | `/bmad-project-context`, puis story BMad | `/opsx:explore`, puis `/opsx:propose` |
| Choisir la profondeur | Tier `S`, `F-lite`, `F-full` | Cycle complet par défaut | Parcours court ou complet selon la story | Change plus ou moins détaillé selon proposal/design |
| Écrire l'intention produit | `artifacts/frames/*`, puis spec si nécessaire | `specs/NNN-slug/spec.md` | Story BMad | `proposal.md` + deltas de spec |
| Clarifier les ambiguïtés | Gate utilisateur pendant `frame` ou `spec` | `/speckit-clarify` | Message de clarification à demander explicitement | Message de clarification à demander avant `design.md` |
| Concevoir techniquement | `analyze` puis `plan` selon le tier | `/speckit-plan` | Demande de plan technique | `design.md` |
| Produire les tests attendus | Plan + exigences + tests par couche | `/speckit-tests` | À exiger dans la story et les tasks | À exiger dans les deltas et `tasks.md` |
| Découper le travail | `artifacts/plans/*-plan.md` | `/speckit-tasks` | Demande explicite de tâches | `tasks.md` |
| Gate humain avant code | Obligatoire selon le tier | À appliquer entre les commandes | À ajouter explicitement avant `/bmad-build` | À ajouter explicitement avant `/opsx:apply` |
| Implémenter | `implement` | `/speckit-implement` | `/bmad-build` | `/opsx:apply` |
| Vérifier localement | `validate` : lint, typecheck, tests | Maven, Cucumber, Hurl, Bruno selon plan | Maven + Hurl à demander explicitement | Maven + Hurl puis `/opsx:verify` |
| Revue | `review`, puis `fix` si nécessaire | `/speckit-analyze`, checklist, convergence | Revue humaine + journal | `/opsx:verify` + revue humaine |
| Rouvrir le travail manquant | `fix` ou reprise `/dev #N` | `/speckit-converge` ajoute des tâches | Demander correction de story/plan/tasks | Corriger proposal/deltas/design/tasks puis réappliquer |
| Livraison | `pr`, `ci-watch`, `ship` selon pipeline | Hors cycle central SpecKit | À organiser avec Git/CI du repo | Archive OpenSpec, puis Git/CI du repo |

## Workflow personnel `/dev`

| Étape | Commande ou phase | Artefacts | Gate | Avantage | Limite |
|---|---|---|---|---|---|
| 1 | `/dev #N` | Issue GitHub comme point d'entrée | Oui | Point d'entrée unique, évite le travail hors cadre | Dépend d'une issue GitHub bien tenue |
| 2 | `triage` | Issue triée | Oui selon cas | Décide vite si c'est bug, feature ou maintenance | Spécifique au workflow personnel |
| 3 | `frame` | `artifacts/frames/{N}-{slug}-frame.md` | Obligatoire pour `F-lite` et `F-full` | Force le problème, les contraintes et le tier | Peut sembler lourd pour une micro-correction |
| 4 | `analyze` | `artifacts/analyses/*` | Pour `F-full` | Évite de planifier sans comprendre l'existant | Skippé sur les petits tiers |
| 5 | `spec` | `artifacts/specs/*` | Obligatoire pour features | Rend les critères d'acceptation explicites | Moins standardisé qu'un framework dédié |
| 6 | `plan` | `artifacts/plans/*` | Obligatoire | Plan cochable et reprise facile | Le format est propre au repo |
| 7 | `implement` | Code + tests | Validation locale | Exécution intégrée au plan | L'agent doit respecter les règles locales |
| 8 | `pr` | PR | Oui | Livraison structurée | Suppose le repo connecté à GitHub |
| 9 | `ci-watch` | Résultat CI | Bloquant | Évite les faux verts locaux | Coût temps incompressible |
| 10 | `validate` | Lint, typecheck, tests | Bloquant | Preuve exécutable | Dépend des scripts du repo |
| 11 | `review` | Findings | Bloquant si findings | Ajoute une revue qualité avant merge | Pas un standard externe portable tel quel |
| 12 | `fix` | Correctifs ciblés | Conditionnel | Ferme les findings sans perdre la trace | Peut rallonger le cycle |
| 13 | `cleanup` | Worktree propre | Conditionnel | Termine proprement le travail | Spécifique au modèle worktree |

**À retenir** : `/dev` est le plus adapté à ton propre système parce qu'il encode tes gates, tes
tiers, tes worktrees, ta CI et ta revue. C'est excellent pour un repo mature, mais moins
directement transmissible : pour l'enseigner, il faut aussi enseigner les règles du repo.

## SpecKit

| Étape | Commande | Artefacts | Gate recommandé | Avantage | Limite |
|---|---|---|---|---|---|
| 0 | `/speckit-constitution` | `.specify/memory/constitution.md` | Oui | Pose les invariants durables avant les features | Demande un vrai travail de sélection des principes |
| 1 | `/speckit-specify` | `specs/NNN-slug/spec.md` | Oui | Sépare clairement le quoi/pourquoi du comment | Peut produire une spec à relire avec exigence |
| 2 | `/speckit-clarify` | `spec.md` enrichi | Oui | Rend les ambiguïtés visibles avant le plan | À ne pas sauter, sinon l'agent devine |
| 3 | `/speckit-plan` | `plan.md`, `research.md`, `data-model.md`, `contracts/` | Oui | Conception structurée et décisions documentées | Peut être trop riche pour une petite feature |
| 4 | `/speckit-tests` | `test-cases.yaml`, Cucumber, `traceability.md` | Oui | Relie critères d'acceptation et preuves | Étape absente ou moins explicite ailleurs |
| 5 | `/speckit-tasks` | `tasks.md` | Oui | Produit un ordre d'exécution traçable | La qualité dépend de la spec et du plan |
| 6 | `/speckit-analyze` | Rapport de cohérence | Recommandé | Détecte les écarts entre spec, plan et tasks | Ne remplace pas un juge externe comme Hurl |
| 7 | `/speckit-implement` | Code + preuves | Validation exécutable | Suit les tâches produites | Peut appliquer une mauvaise tâche si l'amont est faux |
| 8 | `/speckit-converge` | Tâches restantes ajoutées | Oui | Rouvre le travail au lieu de déclarer trop vite | Dépend de ce que les artefacts savent exprimer |
| 9 | `/speckit-checklist` | Checklist ciblée | Optionnel | Renforce un gate qualité | À déclencher volontairement |
| 10 | `/speckit-taskstoissues` | Issues GitHub | Optionnel | Prépare le travail cloud/agents | Utile surtout quand on industrialise |

**À retenir** : SpecKit est le plus complet comme rail SDD. Il fournit des commandes spécialisées
pour chaque transformation importante : intention, clarification, plan, tests, tâches,
implémentation, convergence. C'est le meilleur candidat si l'objectif est d'apprendre une méthode
reproductible et observable.

## BMad

| Étape | Commande ou message | Artefacts | Gate recommandé | Avantage | Limite |
|---|---|---|---|---|---|
| 0 | Terminal : vérifier baseline | Repo propre, pas de `src/` | Oui | Évite de mélanger socle et code généré | Ce contrôle est ajouté par le protocole du repo |
| 1 | `/bmad-project-context` | Contexte projet | Oui | Force l'agent à lire PRD, conformance et instructions | Le nom exact peut varier selon l'installation |
| 2 | `/bmad-spec` | Story BMad | Oui | Format naturel pour cadrer une feature | Moins standardisé que `spec.md` SpecKit |
| 3 | Message de clarification | Questions/decisions | Oui | Souple, adapté au domaine | À demander explicitement |
| 4 | Message de plan technique | Plan par couche | Oui | Permet d'imposer hexagonal, Flyway, Hurl | Pas un artefact aussi normé que SpecKit |
| 5 | Message de tâches | Liste de tâches | Oui | Découpage contrôlable avant build | À renforcer avec les règles du dépôt |
| 6 | Gate humain | Story + plan + tasks relus | Obligatoire | Empêche `/bmad-build` de partir trop tôt | Discipline humaine indispensable |
| 7 | `/bmad-build` | Code + tests + migrations | Validation | Très direct pour exécuter une story | Peut être trop rapide si les artefacts amont sont faibles |
| 8 | Maven + Hurl | Preuves externes | Bloquant | Ramène le verdict au contrat RealWorld | À exiger dans le prompt |
| 9 | Message de correction | Story/plan/tasks corrigés | Oui | Favorise la correction à la source | Moins outillé qu'un `/speckit-converge` |

**À retenir** : BMad paraît moins fourni parce qu'il laisse davantage le dialogue guider le
processus. Son avantage est la souplesse : on peut aller vite sur une story simple ou enrichir le
cycle avec des gates. Pour le laboratoire Conduit, il faut donc le renforcer par des prompts très
explicites et par Hurl.

## OpenSpec

| Étape | Commande ou message | Artefacts | Gate recommandé | Avantage | Limite |
|---|---|---|---|---|---|
| 0 | Terminal : vérifier baseline | Repo propre, pas de `src/` | Oui | Évite de comparer avec du code déjà présent | Contrôle porté par le protocole local |
| 1 | `/opsx:explore` | Contexte projet | Oui | Prépare le changement sans coder | Peut dépendre de l'intégration Copilot installée |
| 2 | `/opsx:propose` | `proposal.md`, deltas, `design.md`, `tasks.md` | Oui | Modèle de change isolé très lisible | Peut générer beaucoup en une fois |
| 3 | Message de durcissement des deltas | Requirements + scenarios | Oui | Sépare comportement observable et design | À demander explicitement si les deltas sont faibles |
| 4 | Message de clarification | Questions/decisions | Oui | Place les décisions avant le design | Discipline humaine nécessaire |
| 5 | Message `design.md` | Design technique | Oui | Garde le comment séparé des deltas | Le design peut dériver si les deltas sont incomplets |
| 6 | Message `tasks.md` | Tâches cochables | Oui | Lie le change à une exécution vérifiable | Moins de cycle tests dédié que SpecKit |
| 7 | `/opsx:apply` | Code + tests + migrations | Validation | Applique un change isolé | À ne lancer qu'après gate humain |
| 8 | `/opsx:verify` | Rapport de vérification | Bloquant | Gate explicite avant archive | Ne remplace pas Maven/Hurl |
| 9 | `/opsx:archive` | Specs vivantes mises à jour | Oui | Excellent mécanisme de mémoire du comportement accepté | Archive dangereuse si les preuves sont faibles |
| 10 | Message de correction | Proposal/deltas/design/tasks corrigés | Oui | Corrige le change à la source | Moins automatique que convergence SpecKit |

**À retenir** : OpenSpec est fort quand on veut garder une mémoire vivante du comportement du
produit. Sa notion de `change` + archive est très intéressante pour des équipes qui veulent
savoir ce qui est accepté aujourd'hui. Il est moins prescriptif que SpecKit sur la dérivation des
tests et la convergence, donc le protocole local doit imposer ces contrôles.

## Comparaison des avantages

| Critère | `/dev` personnel | SpecKit | BMad | OpenSpec |
|---|---|---|---|---|
| Prescriptivité | Très forte dans ton repo | Très forte dans l'outil | Moyenne | Moyenne |
| Portabilité | Moyenne | Forte | Forte | Forte |
| Gates humains | Natifs dans le workflow | À appliquer entre commandes | À ajouter explicitement | À ajouter explicitement |
| Clarification | Selon frame/spec | Commande dédiée | Prompt manuel | Prompt manuel |
| Plan technique | Très adapté au repo | Standardisé | Souple | `design.md` structuré |
| Tests dérivés de l'acceptation | Selon règles locales | Commande dédiée `/speckit-tests` | À exiger | À exiger dans deltas/tasks |
| Convergence | Reprise `/dev`, review/fix | Commande dédiée `/speckit-converge` | Correction manuelle des artefacts | Correction du change puis verify/archive |
| Mémoire projet | Artifacts + règles + ADR | `specs/` + `.specify/` | Stories + journal + règles | Specs vivantes + changes archivés |
| Facilité débutant | Moyenne sans tutoriel | Bonne si on suit les commandes | Variable | Variable |
| Risque de prompt incomplet | Faible dans le repo | Faible à moyen | Moyen à fort | Moyen |

## Choix recommandé selon l'objectif

| Objectif | Meilleur choix | Pourquoi |
|---|---|---|
| Former à un cycle SDD complet | SpecKit | Le cycle est explicite, complet et découpé en commandes spécialisées |
| Industrialiser ton propre repo | `/dev` personnel | Il intègre tes tiers, gates, CI, review et habitudes de livraison |
| Générer vite à partir d'une story | BMad | Le dialogue story/build est direct et souple |
| Maintenir une mémoire de changements acceptés | OpenSpec | Le modèle change -> verify -> archive est très lisible |
| Comparer des frameworks sur Conduit | Les quatre | Même PRD, même stack, même Hurl, puis mesure des écarts |

## Hypothèse à vérifier sur le chantier Conduit

L'hypothèse de départ est la suivante :

> SpecKit devrait produire moins d'oublis méthodologiques au premier passage, parce qu'il fournit
> davantage de prompts spécialisés. BMad et OpenSpec devraient demander plus de discipline humaine,
> mais pourraient être plus souples une fois le protocole local bien écrit.

Cette hypothèse doit être mesurée, pas seulement ressentie. Pour chaque framework, relever sur les
trois premières features Conduit :

| Mesure | Pourquoi elle compte |
|---|---|
| Nombre de questions de clarification utiles | Mesure la capacité à exposer l'implicite |
| Nombre d'échecs Maven au premier passage | Mesure la cohérence technique minimale |
| Nombre d'échecs Hurl au premier passage | Mesure la conformité au contrat externe |
| Nombre de cycles avant vert | Mesure le coût réel du workflow |
| Défauts dus à la spec/story/delta | Mesure la qualité de l'intention |
| Défauts dus au plan/design/tasks | Mesure la qualité de la traduction technique |
| Défauts dus au code généré | Mesure la qualité d'exécution de l'agent |
| Corrections réutilisables dans les instructions | Mesure le retour sur investissement du contexte |

## Sources locales

- [Programme SDD SpecKit](README.md)
- [Palier 3 SpecKit](03-speckit.md)
- [Chantier Conduit SpecKit](04-chantier-conduit.md)
- [Instructions SpecKit](../../.github/instructions/sdd-method.instructions.md)
- [PRD Conduit](../prd/PRD-conduit.md)
- [Contrats de conformité](../../conformance/README.md)
