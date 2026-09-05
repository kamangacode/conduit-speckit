---
title: "Palier 6 — Le kit de coaching"
description: "Transformer une pratique éprouvée en dispositif transmissible : quatre ateliers minutés, une grille de maturité, des métriques honnêtes et une stratégie d'adoption."
date: 2026-09-05
status: ACTIVE
effort: "~8 h"
---

# Palier 6 — Le kit de coaching

> **Objectif** : transformer une compétence personnelle en dispositif transmissible.
>
> **Critère de sortie** : quelqu'un d'autre rejoue l'atelier 1 en autonomie, à partir du
> playbook seul.

---

## 6.0 — La règle qui gouverne ce palier

> **Le playbook ne contient que ce qui a été observé aux paliers 1 à 5.**

Aucun anti-pattern tiré d'un article de blog, aucun chiffre estimé, aucune bonne pratique
recopiée. Chaque affirmation du kit renvoie à une entrée datée de [`journal.md`](journal.md).

La raison est pratique, pas morale. Un coach qui affirme « les specs floues coûtent cher » se
fait répondre « chez nous, non ». Un coach qui montre *« sur l'itération 3, passe A contre
passe B, même code, même juge : 14 échecs de conformité contre 3 »* n'a pas d'objection à
traiter. **Le terrain est le seul argument qui ne se discute pas** — et c'est aussi ce qui
protège le coach le jour où quelque chose ne marche pas comme annoncé.

---

## 6.1 — Les quatre ateliers · ~4 h de rédaction

Chaque atelier va dans `playbook/atelier-N-<slug>.md` avec la même structure : objectif,
durée, prérequis, matériel, déroulé minuté, pièges attendus, critère de réussite.

### Atelier 1 — « Copilot ne devine pas » · 90 min

**Le seul qui compte vraiment, parce qu'il désamorce tout le reste.**

| Temps | Séquence |
|---|---|
| 0-10 | Demander à chacun un contrôleur d'authentification RealWorld, sans contexte. |
| 10-20 | Comparer les sorties. **Toutes utiliseront `Bearer`.** Le contrat impose `Token`. |
| 20-35 | Le point de bascule : *pourquoi tout le monde a la même erreur ?* Parce que le corpus dit `Bearer` et que rien dans le dépôt ne dit le contraire. |
| 35-60 | Écrire ensemble `.github/copilot-instructions.md` avec les invariants RealWorld. |
| 60-80 | Refaire la demande. Mesurer. |
| 80-90 | Généraliser : *quels sont les `Bearer` de notre propre code ?* — les conventions maison qu'aucun corpus ne connaît. |

**Pourquoi cet atelier fonctionne** : il ne demande d'adhérer à rien. Il produit une erreur
que chacun constate sur son propre écran, puis la corrige en vingt minutes. La conclusion — le
contexte du dépôt vaut mieux que la formulation du prompt — s'impose sans être assénée.

**Piège d'animation** : quelqu'un aura utilisé `Token` par chance ou par connaissance de
RealWorld. Ne pas l'écarter — lui demander comment il l'a su. La réponse (« je connais la
spec ») **est** la démonstration : le contexte manquant à Copilot est celui qu'un humain a en
tête et n'a jamais écrit.

**Critère de réussite** : chaque participant a un exemple de convention maison que Copilot ne
peut pas deviner.

### Atelier 2 — « Spec, plan, tasks » · 2 h

Le tri des règles R-1 à R-10 du PRD (l'exercice 2.2), fait en groupe. Le test « la spec
survit-elle à une réécriture en Go ? ». Puis écriture d'une spec à quatre mains sur un
endpoint réel de l'équipe.

**Piège attendu** : le groupe produira une spec truffée de technique. C'est le but — le tri
qui suit est l'apprentissage.

### Atelier 3 — « Le juge externe » · 90 min

Faire écrire à Copilot une implémentation **et** ses tests. Suite verte. Puis lancer Hurl.
Constater l'écart.

**Le seul atelier qui traite la question de la confiance**, et le plus dérangeant. À placer
après l'atelier 2 : trop tôt, il passe pour du procès d'intention envers l'outil.

### Atelier 4 — « Le cycle complet » · 3 h

Une feature réelle de l'équipe, de `/speckit.specify` à la PR. Le coach n'intervient qu'en cas
de blocage dur.

**Critère de réussite** : l'équipe boucle sans le coach. Si elle ne peut pas, le playbook a un
trou — le noter et le combler, c'est l'information la plus utile du palier.

---

## 6.2 — La grille de maturité SDD · ~1 h

Dans `playbook/grille-maturite.md`. Sert à situer chaque développeur et à mesurer la progression
autrement qu'au ressenti.

| Niveau | Nom | Signe distinctif | Ce qui débloque le niveau suivant |
|---|---|---|---|
| **0** | Prompt libre | Décrit ce qu'il veut dans le chat, recommence quand c'est faux | Constater qu'un contexte de dépôt bat une reformulation de prompt (atelier 1) |
| **1** | Contexte outillé | Maintient les fichiers d'instructions, sait pourquoi une consigne est ignorée | Distinguer spec et plan sans hésiter (atelier 2) |
| **2** | Cycle appliqué | Boucle le cycle SpecKit, ne saute pas `/clarify`, corrige la spec plutôt que le code | Répondre « la spec était incomplète » avant « l'IA s'est trompée » |
| **3** | Diagnostic | Devant un défaut, remonte à sa cause (spec / plan / constitution / agent) et corrige à la source | Faire progresser quelqu'un d'autre d'un niveau |

> **Le saut réel est de 2 à 3.** Un développeur de niveau 2 applique un rituel ; un développeur
> de niveau 3 diagnostique. La différence tient à un seul réflexe, mesurable en réunion : devant
> un résultat faux, sa **première** phrase. « L'IA s'est trompée » = niveau 2. « Ma spec ne
> disait rien sur ce cas » = niveau 3.
>
> Un coaching qui amène toute une équipe au niveau 2 est déjà un succès. Viser le niveau 3 pour
> tous est irréaliste ; **viser deux ou trois personnes de niveau 3 qui deviennent les relais
> internes** est la stratégie qui tient dans la durée, parce qu'elle survit au départ du coach.

---

## 6.3 — Les métriques, et celles qu'il faut refuser · ~1 h

Dans `playbook/metriques.md`.

### Ce qu'on mesure

| Métrique | Comment | Ce qu'elle dit |
|---|---|---|
| Taux de conformité au 1er passage | Échecs Hurl sur la 1re exécution | La qualité de l'intention en amont |
| Cycles de régénération jusqu'au vert | Compteur manuel | Le coût réel d'une spec floue |
| Répartition des causes de défaut | spec / plan / constitution / agent | **La plus utile** : elle dit où investir |
| Questions posées par `/clarify` | Compteur | Une spec qui n'en déclenche aucune est soit excellente, soit vide |
| Specs relues en PR avant `/plan` | Ratio | L'adoption réelle du gate social |

### Ce qu'on refuse de mesurer, et pourquoi le dire tout haut

- **Les lignes de code générées.** Mesure le volume, pas la valeur. Une méthode qui divise le
  code par deux à fonctionnalité égale est un succès que cette métrique compterait comme un
  échec.
- **Le temps gagné par développeur.** Non isolable : trop de variables, aucun groupe témoin. Un
  chiffre inventé ici décrédibilise tout le reste du kit.
- **Le taux d'acceptation des suggestions Copilot.** Mesure la complaisance, pas la qualité.
  Accepter moins et mieux est une progression que cette métrique pénalise.

> **Dire explicitement ce qu'on refuse de mesurer, et pourquoi**, est un acte de coaching à part
> entière. C'est ce qui protège l'équipe le jour où la hiérarchie demandera un chiffre de
> productivité — et ce jour viendra. Un coach qui a préparé ce refus, avec ses raisons, aide
> l'équipe ; un coach qui improvise cède.

---

## 6.4 — La stratégie d'adoption · ~1 h

Dans `playbook/adoption.md`. Le kit peut être excellent et l'adoption échouer.

**La séquence, du moins au plus intrusif** :

| Étape | Ce qu'on demande à l'équipe | Pourquoi ça passe |
|---|---|---|
| 1. Copilot code review piloté | **Rien** | La valeur arrive sans effort. Ouvre la conversation sur les instructions. |
| 2. Instructions de dépôt maintenues | Éditer un fichier quand une convention change | Faible coût, bénéfice immédiat et visible |
| 3. Cycle SpecKit sur **une** feature volontaire | Un volontaire, une feature | Pas de mandat. Le résultat parle. |
| 4. Gate social sur les specs | Relire une spec en PR | Ne s'introduit qu'après que l'étape 3 a convaincu |
| 5. Cloud agent sur les tâches fines | Découper `tasks.md` finement | En dernier : demande que tout le reste soit en place |

**L'erreur à ne pas commettre** : commencer par l'étape 3 ou 5 parce que ce sont les plus
spectaculaires. Une équipe à qui l'on impose le cycle complet dès la première semaine le
rejette — non pas parce que la méthode est mauvaise, mais parce que le coût est immédiat et le
bénéfice différé. **La séquence ci-dessus met le bénéfice avant le coût à chaque étape.**

**Le signal d'alerte à surveiller** : si personne ne se porte volontaire à l'étape 3, ne pas
désigner quelqu'un. Ça veut dire que les étapes 1 et 2 n'ont pas convaincu — donc qu'il faut y
retourner, pas forcer.

---

## 6.5 — Le test du kit

**Le kit n'est pas terminé quand il est écrit. Il est terminé quand quelqu'un d'autre l'a
rejoué.**

Prendre une personne qui n'a pas suivi le programme. Lui donner `playbook/atelier-1-*.md`.
Sortir de la pièce. Revenir 90 minutes plus tard.

**Ce qu'il faut noter** : chaque endroit où elle a dû demander de l'aide est un trou du
playbook. Les combler, puis retester avec quelqu'un d'autre.

Tant que ce test n'a pas été fait, le kit est une hypothèse — bien écrite, peut-être juste,
mais non vérifiée. C'est exactement la posture que le programme demande partout ailleurs :
**un artefact non éprouvé ne compte pas.**

---

## 6.6 — Aller plus loin

Trois extensions, dans l'ordre de valeur pédagogique décroissante.

**1. SDD sur du non-fonctionnel — le [volet RGPD](../prd/RGPD/README.md).** Le cas le plus
difficile, et de loin le plus formateur. Comment écrit-on un critère d'acceptation pour
« l'email est chiffré au repos » ou « les droits des personnes sont exerçables en self-service » ?
Les exigences non-fonctionnelles résistent au découpage en user stories, et c'est précisément
là que la plupart des équipes abandonnent SDD. Un coach qui a une réponse sur ce terrain a un
avantage rare.

**2. Le frontend.** Le PRD décrit un frontend complet, avec des e2e Playwright et un
`SELECTORS.md` fourni par RealWorld — donc un second juge externe, sur une nature d'artefact
différente. Bon terrain une fois l'API maîtrisée.

**3. La comparaison inter-outils.** Rejouer l'itération 1 avec Claude Code sur
`conduit-fullstack`, et comparer. SpecKit est agnostique (plus de 30 agents supportés) : mesurer
ce qui vient de la **méthode** et ce qui vient de l'**outil** est le genre de donnée qu'aucun
fournisseur ne publiera. C'est aussi ce qui rend un coach indépendant de son outillage.

---

## Critère de sortie — récapitulatif

- [ ] Les 4 ateliers rédigés dans `playbook/`, avec déroulé minuté et pièges attendus.
- [ ] La grille de maturité écrite, avec le signe distinctif de chaque niveau.
- [ ] Les métriques posées, **y compris celles qu'on refuse**, avec leur justification.
- [ ] La stratégie d'adoption en 5 étapes.
- [ ] **L'atelier 1 rejoué par un tiers, sans aide.** Les trous relevés et comblés.
- [ ] Chaque affirmation du playbook renvoie à une entrée datée de `journal.md`.

---

## Sources

- [`journal.md`](journal.md) — la seule source légitime des anti-patterns du kit
- [Documentation Spec Kit](https://github.github.io/spec-kit/)
- [PRD RGPD Conduit](../prd/RGPD/README.md) — le terrain de l'extension non-fonctionnelle
