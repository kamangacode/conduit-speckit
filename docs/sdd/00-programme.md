---
title: "Le programme détaillé — objectifs, critères de sortie, calendrier"
description: "Vue complète des six paliers : ce qu'il faut savoir faire à la sortie de chacun, comment le prouver, dans quel ordre, et les risques du programme."
date: 2026-09-05
status: ACTIVE
---

# Le programme détaillé

> Ce fichier est la **carte de navigation**. Chaque palier a son fichier dédié avec ses
> exercices. Ici : les objectifs, les critères de sortie vérifiables, le calendrier et les
> risques.

---

## Principe pédagogique : compétence prouvée, pas contenu couvert

Un palier n'est pas « terminé » quand ses pages sont lues. Il est terminé quand son **critère
de sortie** est atteint — et ce critère est toujours formulé comme quelque chose qu'on peut
*montrer*, pas comme quelque chose qu'on *a vu*.

Cette distinction n'est pas cosmétique : c'est exactement ce qu'on va exiger de l'équipe
coachée. Un programme qui s'auto-évalue en pages lues produit des développeurs qui
s'auto-évaluent en prompts envoyés.

---

## Vue d'ensemble

| # | Palier | Objectif | Critère de sortie (vérifiable) | Effort |
|---|---|---|---|---|
| 1 | Socle Copilot | Piloter Copilot, pas le subir | Prédire à l'avance l'effet d'un changement de fichier de customisation, 3 fois sur 3 | 4 h |
| 2 | Méthode SDD | Savoir *pourquoi* avant *comment* | Expliquer en 5 min, sans notes, la différence spec/plan/tasks et pourquoi ce n'est pas du waterfall | 3 h |
| 3 | SpecKit mécanique | Connaître l'outil de bout en bout | Un cycle complet bouclé sur une feature jetable + les 21 rules réparties sur 3 couches | 5 h |
| 4 | Chantier Conduit | Éprouver la méthode sur du vrai | La suite Hurl RealWorld passe au vert sur auth + articles | 16 h |
| 5 | Industrialisation | Passer de l'atelier à la chaîne | Une issue assignée au cloud agent produit une PR relue automatiquement et mergeable | 6 h |
| 6 | Kit de coaching | Transmettre | Un tiers rejoue l'atelier 1 depuis le playbook, sans aide | 8 h |

---

## Palier 1 — Socle Copilot · ~4 h

**Objectif** : cesser de traiter Copilot comme une boîte noire à qui l'on parle, commencer à le
traiter comme un système qu'on configure.

**À savoir faire en sortie** :
- Citer les 4 surfaces (IDE, cloud agent, code review, CLI) et ce que chacune honore comme
  fichiers de contexte — c'est **le** point qui bloque le plus les équipes.
- Écrire un `.github/copilot-instructions.md` qui change un comportement observable.
- Écrire un `.github/instructions/*.instructions.md` avec `applyTo`, et savoir quelle surface
  le lit vraiment.
- Distinguer `AGENTS.md` de `copilot-instructions.md` et savoir lequel s'applique où.

**Critère de sortie** : 3 prédictions correctes d'affilée sur l'effet d'un changement de
configuration. Consigner les 3 dans [`journal.md`](journal.md).

**Piège principal** : croire que tous les fichiers sont lus par toutes les surfaces. Ils ne le
sont pas. Détail dans [`reference/copilot-customisation.md`](reference/copilot-customisation.md).

→ [`01-socle-copilot.md`](01-socle-copilot.md)

---

## Palier 2 — Méthode SDD · ~3 h

**Objectif** : comprendre l'inversion conceptuelle avant de toucher à l'outil, pour ne pas
réduire SDD à « une suite de slash commands ».

**À savoir faire en sortie** :
- Énoncer l'inversion : *« Le PRD n'est pas un guide pour l'implémentation ; c'est la source
  qui génère l'implémentation. »*
- Défendre la frontière **spec ≠ plan ≠ tasks** contre les deux confusions les plus courantes
  (mettre du technique dans la spec, mettre du fonctionnel dans le plan).
- Réfuter l'objection « c'est du waterfall déguisé » avec un argument solide, pas un slogan.
- Mapper chaque étape SDD sur le pipeline maison de `conduit-fullstack`.

**Critère de sortie** : une explication de 5 minutes, sans notes, dite à voix haute. Si elle
demande plus de 5 minutes, la compréhension n'est pas encore condensée.

**Piège principal** : traverser ce palier en lecture rapide parce que « SDD, c'est juste écrire
la spec d'abord ». Cette formule est fausse et produit des équipes qui écrivent des specs
techniques inexploitables.

→ [`02-methode-sdd.md`](02-methode-sdd.md)

---

## Palier 3 — SpecKit, la mécanique · ~5 h

**Objectif** : connaître l'outil dans ses détails, y compris ce qu'il ne fait pas.

**À savoir faire en sortie** :
- Installer, initialiser, choisir entre le layout *skills* et le layout *commands*.
- Citer les 10 commandes, leur ordre, et lesquelles sont optionnelles.
- Dire quel artefact produit quelle commande, et où il atterrit.
- **Répartir une convention sur la bonne couche** : constitution (décisions) / instructions
  scopées (frappes) / presets (réutilisation inter-dépôts). C'est la question qui vient
  systématiquement quand une équipe arrive de rules modulaires.
- Repérer les **gates manquants** : SpecKit enchaîne sans forcer de validation humaine ; c'est
  la principale différence avec le pipeline maison de `conduit-fullstack`, et le principal
  risque en équipe.

**Critère de sortie** : un cycle complet bouclé sur une feature **jetable** — pas Conduit,
quelque chose de trivial du type « endpoint de healthcheck » — avec tous les artefacts produits
et lus. Puis tout jeter. Le but est la mécanique, pas le résultat. Plus la répartition des
21 rules de `conduit-fullstack` sur les trois couches, justifiée.

**Piège principal** : commencer directement sur Conduit. La première exécution sert à
découvrir l'outil ; y mêler un enjeu réel brouille les deux apprentissages.

→ [`03-speckit.md`](03-speckit.md)

---

## Palier 4 — Le chantier Conduit · ~16 h

**Le cœur du programme.** Trois itérations sur un périmètre réduit, avec un juge externe.

**Périmètre** : `F-AUTH-1..4` + `F-ART-1,3,4,5,6` du [PRD](../prd/PRD-conduit.md) — inscription,
connexion, utilisateur courant, mise à jour ; puis lister / consulter / créer / éditer /
supprimer un article. **Stack : Java 21 + Spring Boot.**

**Les trois itérations** :

| Itération | Feature | Ce qui s'apprend |
|---|---|---|
| 1 | `001-auth-jwt` | Le cycle complet, la constitution, le premier `/speckit.clarify` |
| 2 | `002-articles-crud` | Le découpage, la réutilisation du contexte, `/speckit.analyze` |
| 3 | `003-articles-listing` | Filtres et pagination — là où les specs floues font mal |

**Critère de sortie** : `HOST=http://localhost:8080/api ./run-api-tests-hurl.sh` au vert sur le
périmètre couvert. Pas « les tests que Copilot a écrits passent » — **les tests de RealWorld**,
que personne dans la boucle n'a écrits.

**Piège principal** : accepter la première spec générée. La valeur de `/speckit.clarify` est
proportionnelle au sérieux avec lequel on répond à ses questions.

→ [`04-chantier-conduit.md`](04-chantier-conduit.md)

---

## Palier 5 — Industrialisation · ~6 h

**Objectif** : sortir de l'atelier individuel. C'est le palier qui décide si une équipe adopte
ou abandonne.

**À savoir faire en sortie** :
- Convertir un `tasks.md` en issues GitHub (`/speckit.taskstoissues`) et en assigner au cloud
  agent.
- Piloter Copilot code review par des `.instructions.md` ciblés (`applyTo`) — le levier le plus
  sous-estimé pour un coach.
- Connaître les **limites dures** du cloud agent : 59 minutes non extensibles, un dépôt et une
  branche par session, blocage possible par les rulesets et les branch protections.
- Câbler une CI qui vérifie la conformité Hurl à chaque PR.

**Critère de sortie** : une issue issue de `tasks.md`, assignée à Copilot, qui produit une PR
relue automatiquement, avec la CI Hurl verte.

**Piège principal** : lancer le cloud agent sur des tâches trop grosses. La limite de 59 min
n'est pas négociable ; le découpage de `tasks.md` doit en tenir compte **en amont**.

→ [`05-industrialisation.md`](05-industrialisation.md)

---

## Palier 6 — Kit de coaching · ~8 h

**Objectif** : transformer une compétence personnelle en dispositif transmissible.

**À produire dans [`playbook/`](playbook/)** :
- 4 ateliers minutés, avec matériel, déroulé et pièges attendus.
- Une grille de maturité SDD en 4 niveaux, pour situer chaque développeur.
- Le catalogue d'anti-patterns, alimenté par ce qui a *réellement* été observé aux paliers 1-5.
- Les métriques d'adoption : ce qu'on mesure, ce qu'on refuse de mesurer, et pourquoi.

**Critère de sortie** : quelqu'un d'autre rejoue l'atelier 1 en autonomie, à partir du playbook
seul. Tant que ça n'a pas été testé sur un vrai cobaye, le kit est une hypothèse.

**Piège principal** : écrire le playbook depuis la théorie plutôt que depuis les échecs
rencontrés. Les anti-patterns crédibles viennent de [`journal.md`](journal.md), pas d'un
article de blog.

→ [`06-kit-coaching.md`](06-kit-coaching.md)

---

## Calendrier proposé — 6 semaines à ~7 h

| Semaine | Contenu | Livrable de fin de semaine |
|---|---|---|
| S1 | Paliers 1 + 2 | Repo instrumenté · explication SDD de 5 min |
| S2 | Palier 3 + démarrage palier 4 | Cycle jetable bouclé · constitution Conduit écrite |
| S3 | Palier 4, itérations 1 et 2 | Auth JWT conforme Hurl |
| S4 | Palier 4, itération 3 | Articles CRUD + listing conformes Hurl |
| S5 | Palier 5 | Boucle issue → PR → review → CI verte |
| S6 | Palier 6 | Playbook testé sur un cobaye |

**Règle de rythme** : mieux vaut 4 sessions de 2 h qu'une session de 8 h. Le SDD s'apprend par
répétition du cycle, et un cycle a besoin de temps de décantation entre l'écriture d'une spec
et la revue de ce qu'elle a produit.

---

## Risques du programme, et leur parade

| Risque | Pourquoi il est réel | Parade |
|---|---|---|
| **L'outil bouge sous les pieds** | 3 releases SpecKit en 3 jours (v1.0.2 → v1.0.4, fin août / début septembre 2026) | Épingler la version (`@v1.0.4`) pour toute la durée du programme. Ne mettre à jour qu'entre deux paliers, jamais au milieu d'une itération. |
| **Le confort de la démo** | Conduit est un domaine simple ; SDD y brille facilement | L'itération 3 (filtres + pagination) fait mal pour de vrai. Ne pas la sauter : c'est celle qui donne les arguments de coach. |
| **L'IA note sa propre copie** | Copilot écrit le code *et* ses tests | Le juge est Hurl, écrit par RealWorld. Non négociable. |
| **Confondre outillage et méthode** | Le plus courant : l'équipe apprend 10 slash commands et rien d'autre | Le palier 2 précède le palier 3, délibérément. Un développeur qui sait *pourquoi* rattrape un outil qui change ; l'inverse est faux. |
| **Le kit écrit depuis la théorie** | Tentation de finir vite au palier 6 | Le playbook ne cite que des anti-patterns tracés dans `journal.md` avec une date. |
| **Adoption imposée par le haut** | Un coach mandaté, une équipe non demandeuse | Commencer par le levier le moins intrusif (Copilot code review piloté par `.instructions.md`) : il donne de la valeur sans rien changer aux habitudes de travail. |

---

## Ce que ce programme ne couvre pas

À dire explicitement, pour ne pas laisser croire à une couverture qu'il n'a pas :

- **Le frontend.** Le PRD Conduit décrit un frontend complet (routes, templates, e2e
  Playwright). Le programme s'arrête à l'API. Raison : le cycle SDD s'observe plus proprement
  sur un contrat d'API vérifiable par une suite externe. Le frontend est un bon **second**
  terrain, une fois la méthode acquise.
- **Le volet RGPD.** Le [sous-PRD RGPD](../prd/RGPD/README.md) est un excellent terrain pour
  éprouver SDD sur du **non-fonctionnel** — le cas le plus difficile. Gardé délibérément en
  réserve comme extension (voir palier 6, « aller plus loin »).
- **L'évaluation du ROI de Copilot.** Mesurer l'impact d'un assistant sur la productivité d'une
  équipe est un sujet en soi, méthodologiquement piégeux. Le palier 6 propose des métriques
  d'**adoption** et de **qualité de spec**, pas de productivité.
