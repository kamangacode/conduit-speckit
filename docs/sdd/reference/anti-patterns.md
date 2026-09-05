---
title: "Référence — anti-patterns SDD"
description: "Catalogue des pièges, avec symptôme, cause et correction. Les entrées prédites sont marquées comme telles jusqu'à confirmation sur le terrain."
date: 2026-09-05
status: DRAFT
---

# Référence — anti-patterns SDD

> **Statut de ce fichier : brouillon assumé.**
>
> Les entrées ci-dessous sont **prédites**, pas observées. Elles proviennent de la conception
> du programme et de la documentation, pas encore du terrain. Chacune porte un marqueur :
>
> - 🔮 **prédit** — hypothèse à confirmer ou infirmer pendant les paliers 1 à 5
> - ✅ **confirmé** — rencontré, daté dans [`../journal.md`](../journal.md)
> - ❌ **infirmé** — ne s'est pas produit, ou pas comme prévu
>
> **Le playbook du [palier 6](../06-kit-coaching.md) ne reprend que les ✅.** Un catalogue
> d'anti-patterns théoriques est un catalogue de bonnes pratiques recopiées : il ne survit pas
> à la première objection d'une équipe qui dit « chez nous, ça ne se passe pas comme ça ».

---

## A. Anti-patterns de spécification

### A1 🔮 — Le plan qui fuit dans la spec

**Symptôme** : `spec.md` nomme des frameworks, des classes, des tables, des signatures.

**Pourquoi ça arrive** : c'est le réflexe naturel d'un développeur. On pense en solutions.

**Le coût** : la spec devient illisible pour un non-développeur. Le PO ne peut plus la valider,
donc plus personne ne la valide, donc elle cesse d'être un contrat partagé.

**Le test** : *la spec reste-t-elle vraie si on réécrit l'application dans un autre langage ?*

**Correction** : déplacer le contenu technique dans `plan.md`. Ne pas le supprimer — il est
souvent juste, simplement mal placé.

> **La nuance qui fait la différence** : la frontière n'est pas « fonctionnel vs technique »,
> elle est **observable de l'extérieur vs choix interne**. `Authorization: Token <jwt>` est
> technique **et** appartient à la spec, parce que c'est un contrat observable imposé par
> RealWorld. « On signe avec `jjwt` » est du plan.

---

### A2 🔮 — Le fonctionnel qui descend dans le plan

**Symptôme** : `plan.md` introduit un comportement absent de `spec.md`.

**Le coût** : ce comportement n'a **aucun critère d'acceptation**. Il sera implémenté, et
jamais vérifié.

**Pourquoi c'est le plus dangereux** : la fuite A1 se voit à la lecture. Celle-ci ne se voit
pas — il faut comparer deux documents pour la repérer. C'est exactement ce que cherche
`/speckit.analyze`.

**Correction** : remonter le comportement dans la spec, avec son critère d'acceptation.

---

### A3 🔮 — Sauter `/speckit.clarify`

**Symptôme** : on passe de `specify` à `plan` sans clarification, « parce que la spec paraît
claire ».

**Le coût** : chaque silence de la spec devient une hypothèse **silencieuse** de l'agent. Elle
ne sera signalée nulle part et ne sera couverte par aucun test.

**Correction** : ne jamais sauter. Si `/clarify` ne pose aucune question sur une spec non
triviale, c'est un signal — soit la spec est excellente, soit elle est trop vague pour que
l'outil sache quoi demander. Vérifier laquelle des deux.

---

### A4 🔮 — La spec muette sur la sécurité

**Symptôme** : la spec dit qui a le droit de faire quoi, mais pas ce qui se passe quand le
droit manque.

**Cas d'école Conduit** : sur un article inexistant, faut-il 404 ou 403 ? Le PRD ne le dit pas.
Un 403 sur une ressource absente **révèle son existence** — fuite d'information par canal
auxiliaire.

**Le coût, et pourquoi il est spécifique** : une spec incomplète sur un point fonctionnel
produit un bug qu'un test attrape. Une spec incomplète sur un point de sécurité produit **une
faille**, et rien ne la signale — ni le compilateur, ni les tests, ni la revue.

**Correction** : pour chaque règle d'autorisation, exiger la réponse aux trois cas — autorisé,
interdit, ressource absente.

---

## B. Anti-patterns de constitution

### B1 🔮 — La constitution décorative

**Symptôme** : des principes que personne n'a l'intention de faire respecter.

**Le coût** : une règle non appliquée dégrade toutes les autres. Elle apprend au lecteur —
humain ou agent — que cette liste est indicative.

**Le filtre à trois critères** : vérifiable · corrige un penchant par défaut · on est prêt à
refuser du code qui l'enfreint. **Le troisième est celui qu'on oublie.**

---

### B2 🔮 — La constitution figée

**Symptôme** : écrite au jour 1, jamais rouverte.

**Le coût** : les corrections qui auraient dû y remonter restent locales à une feature et ne
profitent pas aux suivantes.

**Correction** : à chaque défaut récurrent, se demander *« est-ce que cet invariant vaut pour
tout le projet ? »* Si oui, il remonte dans la constitution. C'est le mécanisme même du
retour sur investissement mesuré au [palier 4](../04-chantier-conduit.md).

---

## C. Anti-patterns d'exécution

### C1 🔮 — Patcher le code au lieu de la source

**Symptôme** : le résultat est faux, on corrige le code à la main, on continue.

**Le coût** : le défaut d'intention reste. Il reproduira le même symptôme à la prochaine
feature, et l'information sur sa cause réelle est perdue.

**Correction** : identifier le niveau (spec / plan / constitution), corriger là, régénérer.

> **Nuance de terrain** : en production, on patche — c'est légitime, on a des délais. La règle
> stricte du palier 4 est un **régime d'apprentissage**, pas une règle de vie. À dire tel quel
> à l'équipe : présenter une contrainte pédagogique comme un dogme professionnel fait perdre
> la confiance de ceux qui savent qu'elle est intenable.

---

### C2 🔮 — Le juge intérieur

**Symptôme** : l'agent écrit le code **et** ses tests. Suite verte, couverture élevée, API
fausse.

**Pourquoi c'est traître** : tous les indicateurs habituels sont au vert. Rien n'alerte.

**Correction** : un juge écrit par un tiers, avant que le code existe. Pour Conduit, la suite
Hurl de RealWorld. Sans équivalent externe : au minimum, quelqu'un d'autre que l'auteur du code
écrit les cas de test — à partir de la **spec**, pas du code.

---

### C3 🔮 — La tâche trop grosse pour le cloud agent

**Symptôme** : l'agent atteint 59 minutes et échoue **sans livrer**.

**Correction** : ça ne se corrige pas au moment de l'assignation, mais au moment de
`/speckit.tasks`, en exigeant des tâches plus fines. Bonne illustration du principe SDD — un
problème d'exécution se traite dans l'artefact qui l'a produit.

---

## D. Anti-patterns d'équipe

### D1 🔮 — Le cycle sans gate

**Symptôme** : un développeur enchaîne `/specify → /plan → /tasks → /implement` en vingt
minutes et produit 2 000 lignes que personne n'a cadrées.

**Pourquoi ça arrive** : SpecKit n'impose aucun arrêt entre ses commandes. En solo, ça passe.

**Correction** : le gate social (spec relue en PR avant `/plan`) ou le gate outillé
(`/speckit.checklist`). Le premier traite la cause, le second est plus rapide mais laisse
l'agent juge et partie.

---

### D2 🔮 — L'adoption par mandat

**Symptôme** : « à partir de lundi, on fait du SDD ».

**Le coût** : le coût est immédiat, le bénéfice différé. L'équipe rejette — pas la méthode,
mais le calendrier qu'on lui impose.

**Correction** : la séquence du [palier 6](../06-kit-coaching.md), qui met le bénéfice avant le
coût à chaque étape, et commence par ce qui ne demande rien à personne (la code review pilotée
par instructions).

---

### D3 🔮 — Les instructions contradictoires

**Symptôme** : le comportement de Copilot devient erratique après que deux personnes ont édité
la configuration sans se parler.

**Pourquoi c'est piégeux** : le développeur qui le constate dira « Copilot est devenu
incohérent », jamais « nos instructions se contredisent ». **Le diagnostic est
contre-intuitif** — c'est ce qui en fait une compétence de coach.

**Correction** : la configuration se relit en PR comme du code. Et connaître à l'avance ce que
fait Copilot en cas de conflit (manche 3 de l'exercice 1.3).

---

## E. Anti-patterns d'outillage

### E1 🔮 — La version flottante

**Symptôme** : SpecKit installé depuis la branche principale. Les templates changent en cours
de programme.

**Le coût** : on ne sait plus si un résultat a changé parce que la spec était mauvaise ou parce
que l'outil a bougé. Un échec de méthode devient une énigme d'outillage.

**Correction** : épingler (`@v1.0.4`) pour toute la durée. Mettre à jour **entre** deux paliers,
jamais au milieu d'une itération.

---

### E2 🔮 — La consigne dans le mauvais fichier

**Symptôme** : une consigne écrite dans un fichier que la surface visée ne lit pas. « Copilot
n'écoute pas. » Retour au prompt libre.

**Le coût** : c'est l'anti-pattern qui **annule tout le bénéfice de SDD**, parce qu'il fait
conclure à l'équipe que la configuration ne sert à rien.

**Correction** : [`copilot-customisation.md`](copilot-customisation.md), et vérifier les cases
`?` sur son propre tenant plutôt que de les supposer.

---

## Registre de confirmation

> À tenir à jour pendant les paliers 1 à 5. Une entrée passe en ✅ ou ❌ avec une date et un
> renvoi vers [`../journal.md`](../journal.md).

| ID | Anti-pattern | Statut | Date | Renvoi journal |
|---|---|---|---|---|
| A1 | Le plan fuit dans la spec | 🔮 | | |
| A2 | Le fonctionnel descend dans le plan | 🔮 | | |
| A3 | `/clarify` sauté | 🔮 | | |
| A4 | Spec muette sur la sécurité | 🔮 | | |
| B1 | Constitution décorative | 🔮 | | |
| B2 | Constitution figée | 🔮 | | |
| C1 | Patcher le code au lieu de la source | 🔮 | | |
| C2 | Le juge intérieur | 🔮 | | |
| C3 | Tâche trop grosse pour le cloud agent | 🔮 | | |
| D1 | Cycle sans gate | 🔮 | | |
| D2 | Adoption par mandat | 🔮 | | |
| D3 | Instructions contradictoires | 🔮 | | |
| E1 | Version flottante | 🔮 | | |
| E2 | Consigne dans le mauvais fichier | 🔮 | | |

**Les entrées restées 🔮 à la fin du palier 5 ne vont pas dans le playbook.** Elles restent
ici, comme hypothèses non éprouvées — ce qui est une information en soi.
