---
title: "Palier 2 — La méthode SDD"
description: "L'inversion spec/code, la frontière spec ≠ plan ≠ tasks, la réfutation de l'objection waterfall, et le mapping vers le pipeline maison de conduit-fullstack."
date: 2026-09-05
status: ACTIVE
effort: "~3 h"
---

# Palier 2 — La méthode SDD

> **Objectif** : comprendre l'inversion conceptuelle **avant** de toucher à l'outil, pour ne pas
> réduire SDD à « une suite de slash commands ».
>
> **Critère de sortie** : une explication de 5 minutes, sans notes, dite à voix haute.

---

## Pourquoi ce palier avant l'outil

Un développeur qui sait *pourquoi* rattrape un outil qui change. L'inverse est faux — et
SpecKit a sorti trois versions en trois jours fin août 2026.

Il y a aussi une raison de coaching, plus dure : **la résistance à SDD n'est jamais technique.**
Personne ne dit « je n'arrive pas à taper `/speckit.specify` ». Les objections réelles sont
« on va perdre du temps à écrire au lieu de coder », « c'est du cycle en V déguisé », « nos
specs seront périmées en deux semaines ». On n'y répond pas avec un tutoriel. On y répond en
ayant compris ce que SDD déplace exactement.

---

## 2.1 — L'inversion

La formulation de GitHub, à connaître mot pour mot :

> *« Code serves specifications. The Product Requirements Document isn't a guide for
> implementation; it's the source that generates implementation. »*

Et son corollaire, qui est le vrai contenu de la méthode :

> *« The specification becomes the primary artifact. Code becomes its expression in a particular
> language and framework. »*
>
> *« Maintaining software means evolving specifications. »*

**Ce que ça déplace concrètement.** Dans le développement classique, la spec est un échafaudage :
utile pendant la construction, jetée après. La vérité migre vers le code dès la première ligne
écrite, et l'écart entre les deux ne fait que croître — c'est le *specification-implementation
gap*, la raison pour laquelle personne ne fait confiance à une doc de plus de six mois.

SDD supprime l'écart en supprimant la dualité : *« When specifications and implementation plans
generate code, there is no gap — only transformation. »*

**Le test qui vérifie qu'on a compris** : dans un projet SDD, quand une règle métier change, où
va-t-on éditer en premier ? Si la réponse est « dans le code, et je mettrai la doc à jour
après », l'inversion n'a pas eu lieu — on fait du développement classique avec des fichiers
Markdown en plus.

---

## 2.2 — La frontière spec ≠ plan ≠ tasks

C'est **la** compétence discriminante de ce palier. Les trois artefacts répondent à trois
questions différentes, et les mélanger est l'échec le plus courant.

| Artefact | Question | Contient | Ne contient **jamais** |
|---|---|---|---|
| `spec.md` | **Quoi** et **pourquoi** | User stories, comportements observables, critères d'acceptation, cas limites | Un nom de framework, un schéma de table, une signature de méthode |
| `plan.md` | **Comment** | Stack, architecture, modèle de données, contrats, **et la justification de chaque choix** | Des exigences fonctionnelles nouvelles |
| `tasks.md` | **Dans quel ordre** | Unités de travail exécutables, ordonnées, avec leurs dépendances | Des décisions d'architecture non tranchées en amont |

### La règle qui tient debout toute seule

> **Une spec doit rester vraie si on réécrit l'application dans un autre langage.**

C'est le test le plus rapide et il ne trompe pas. « L'utilisateur reçoit un jeton qui
l'authentifie sur les requêtes suivantes » survit à une réécriture en Go. « Le `AuthService`
appelle `JwtEncoder.encode()` » n'y survit pas : c'est du plan qui a fui dans la spec.

Appliqué à Conduit, la nuance est instructive : le PRD dit que l'en-tête est
`Authorization: Token <jwt>`. **Est-ce de la spec ou du plan ?** C'est de la **spec** — parce
que c'est un contrat observable de l'extérieur, imposé par RealWorld, et qu'il reste vrai
quelle que soit l'implémentation. En revanche « on signe le JWT avec la bibliothèque `jjwt` »
est du plan. La frontière n'est pas « fonctionnel vs technique », elle est **observable de
l'extérieur vs choix interne**. C'est plus fin, et c'est ce qui rend le sujet coachable.

### Les deux fuites à traquer

| Fuite | Symptôme | Coût |
|---|---|---|
| **Le technique remonte dans la spec** | La spec nomme des classes, des tables, des libs | La spec devient invalidable par un non-développeur : le PO ne peut plus la relire, donc plus personne ne la relit |
| **Le fonctionnel descend dans le plan** | Le plan introduit un comportement absent de la spec | Ce comportement n'a **aucun critère d'acceptation**. Il sera implémenté et jamais vérifié |

La seconde est la plus dangereuse et la moins visible. C'est précisément ce que
`/speckit.analyze` cherche : la cohérence entre artefacts.

**Exercice 2.2** (45 min) — Prendre la section 11 du [PRD](../prd/PRD-conduit.md) (les règles
R-1 à R-10) et classer chaque règle en `spec` ou `plan`, avec une justification en une ligne.
R-1 (le slug est généré depuis le titre en kebab-case) est le cas intéressant : argumenter les
deux positions avant de trancher. Consigner dans [`journal.md`](journal.md).

---

## 2.3 — « C'est du waterfall déguisé » — la réfutation

L'objection viendra. Une réponse en trois temps, du plus faible au plus fort.

**Temps 1 — le cycle n'est pas linéaire.** GitHub le formule ainsi :
*« This process is therefore a 0 → 1, (1', ..), 2, 3, N. »* Un premier jet (0→1), des variantes
explorées en parallèle (1'), puis l'enrichissement itératif. La commande `/speckit.converge`
existe exactement pour ça : elle confronte le code réel à la spec et **rouvre du travail**. Un
waterfall n'a pas de commande qui rouvre la phase précédente.

**Temps 2 — le coût de l'écrit s'est effondré.** Le waterfall échouait pour une raison
économique : réviser un document de 80 pages coûtait des semaines, donc on ne révisait pas,
donc le document mentait. Quand la révision d'une spec coûte dix minutes et que la
régénération du code en découle, la logique s'inverse. **SDD n'est pas le waterfall qui
reviendrait ; c'est ce que le waterfall aurait été si l'écrit avait été bon marché.**

**Temps 3 — le plus solide, parce qu'il est vérifiable.** En waterfall, la spec et le code
divergent parce que rien ne les relie mécaniquement. En SDD, la spec **génère** le code : la
divergence se voit à la régénération suivante. La spec n'est pas un document de phase, c'est
un artefact vivant du dépôt, versionné, relu en PR, qui casse quand il ment.

> **Honnêteté de coach — les limites à énoncer.** Un coach qui ne présente que les forces perd
> sa crédibilité à la première difficulté rencontrée par l'équipe. Trois limites réelles :
>
> 1. **Sur un très gros existant, SDD ne rétro-documente pas.** La doc SpecKit le dit :
>    l'initialisation « ne réécrit pas l'application et n'infère pas de specs pour le
>    comportement existant ». Le premier chantier doit être **borné**, pas « documenter le
>    système ».
> 2. **Le coût se déplace, il ne disparaît pas.** On passe du temps à écrire et clarifier ce
>    qu'on passait à déboguer et refaire. Le gain est réel mais il est *différé* — c'est
>    exactement le profil d'investissement qu'une équipe sous pression refuse.
> 3. **Une spec floue produit du code faux plus vite qu'avant.** SDD amplifie la qualité de
>    l'intention, dans les deux sens. C'est ce que l'itération 3 du [palier 4](04-chantier-conduit.md)
>    va montrer sur les filtres et la pagination.

---

## 2.4 — Le mapping vers ce qui existe déjà

Le pipeline `/dev` de `conduit-fullstack` est du SDD artisanal. Poser le mapping explicitement
sert deux fois : ça accélère l'apprentissage, et ça donne **le meilleur support de coaching
possible** — une équipe adopte plus volontiers une méthode qu'on lui présente comme la
formalisation d'une pratique qu'elle a déjà à moitié, que comme une révolution.

| `conduit-fullstack` (`/dev`) | SpecKit | Écart notable |
|---|---|---|
| `.claude/rules/` (21 fichiers scopés) | **Trois mécanismes, pas un** : constitution + instructions scopées + presets | Tes rules font deux métiers (gouverner une *décision* / gouverner l'*écriture d'un fichier*) que SpecKit sépare. Détail en [§3.6 du palier 3](03-speckit.md). |
| `frame` (problème, contraintes, tier) | `/speckit.specify` | SpecKit n'a pas de notion de **tier** (S / F-lite / F-full). Tout passe par le cycle complet. |
| `spec` (acceptance criteria) | `/speckit.specify` + `/speckit.clarify` | `/clarify` est **meilleur** : il pose des questions ciblées au lieu de laisser l'humain deviner ce qui manque. |
| `analyze` (F-full) | `/speckit.plan` + `research.md` | Équivalent. |
| `plan` (items cochables) | `/speckit.tasks` | Équivalent. |
| `implement` | `/speckit.implement` | Équivalent. |
| `review` + `validate` | `/speckit.analyze` + `/speckit.checklist` | `/analyze` vérifie la **cohérence entre artefacts**, ce que la revue maison ne fait pas. Un vrai apport. |
| ADRs (`docs/adr/`) | Le rationale dans `plan.md` | L'ADR **numéroté et permanent** est supérieur pour la mémoire longue. À conserver en plus. |
| **Gates utilisateur obligatoires** | *rien* | **L'écart le plus important. Voir ci-dessous.** |

### Le trou à combler : les gates

Ton pipeline maison impose une validation humaine après `frame`, `spec` et `plan`. SpecKit
enchaîne les commandes sans forcer d'arrêt. Sur un chantier solo, ça passe. **En équipe, c'est
le risque principal** : un développeur enchaîne `/specify → /plan → /tasks → /implement` en
vingt minutes et produit 2 000 lignes que personne n'a cadrées, à partir d'une spec que
personne n'a lue.

C'est le trou que ton coaching doit combler explicitement. Deux parades, à éprouver au
[palier 4](04-chantier-conduit.md) :

1. **Le gate social** : la spec est poussée en PR et relue *avant* `/speckit.plan`. Lent, mais
   c'est le seul qui traite la cause.
2. **Le gate outillé** : `/speckit.checklist` génère une checklist de qualité de spec, dont la
   validation conditionne le passage à l'étape suivante. Plus rapide, mais l'agent reste juge
   et partie.

**Exercice 2.4** (30 min) — Compléter ce tableau avec deux colonnes : « ce que SpecKit fait
mieux » et « ce que le dispositif maison fait mieux ». Cette liste est la matière première de
ta légitimité de coach : elle prouve que tu as évalué l'outil au lieu de le réciter.

---

## 2.5 — Le livrable du palier

Une note de synthèse dans `journal.md`, qui répond à cinq questions **sans regarder ce
fichier** :

1. Quelle est l'inversion, en une phrase ?
2. Quelle est la frontière spec / plan, et quel test la tranche ?
3. Pourquoi ce n'est pas du waterfall — trois arguments ?
4. Quelles sont les trois limites honnêtes de SDD ?
5. Quel est le trou de SpecKit par rapport à un pipeline avec gates, et comment le combler ?

Puis dire l'ensemble à voix haute, chronomètre en main. **Cinq minutes maximum.** Au-delà, la
compréhension existe mais n'est pas condensée — et une compréhension non condensée ne se
transmet pas : elle se récite.

---

## Critère de sortie — récapitulatif

- [ ] Les règles R-1 à R-10 du PRD sont classées spec / plan, avec justification.
- [ ] Le cas R-1 (slug) est argumenté des deux côtés avant d'être tranché.
- [ ] Le tableau de mapping est complété avec les deux colonnes comparatives.
- [ ] Les cinq réponses tiennent en 5 minutes à voix haute, sans notes.

→ Palier suivant : [`03-speckit.md`](03-speckit.md)

---

## Sources

- [`spec-driven.md` — la philosophie SDD, dépôt github/spec-kit](https://github.com/github/spec-kit/blob/main/spec-driven.md)
- [Documentation Spec Kit](https://github.github.io/spec-kit/)
- [Adopter Spec Kit sur un projet existant](https://github.github.io/spec-kit/guides/existing-projects.html)
