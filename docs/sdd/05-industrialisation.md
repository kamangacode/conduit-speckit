---
title: "Palier 5 — Industrialisation"
description: "Sortir de l'atelier individuel : tasks.md en issues, cloud agent, Copilot code review piloté par instructions, CI de conformité. Et les limites dures à connaître."
date: 2026-09-05
status: ACTIVE
effort: "~6 h"
---

# Palier 5 — Industrialisation

> **Objectif** : passer de l'atelier individuel à une chaîne d'équipe. C'est le palier qui
> décide si une équipe adopte SDD ou l'abandonne après trois semaines.
>
> **Critère de sortie** : une issue issue de `tasks.md`, assignée au cloud agent, qui produit
> une PR relue automatiquement, avec la CI Hurl verte.

---

## 5.0 — Pourquoi ce palier fait basculer l'adoption

SDD en solo est agréable : on écrit une spec, on génère, on ajuste. Rien n'oblige à formaliser
quoi que ce soit.

En équipe, trois problèmes apparaissent d'un coup, et aucun n'est technique :

1. **Qui valide la spec ?** Sans gate, un développeur enchaîne `/specify → /implement` en
   vingt minutes et produit 2 000 lignes que personne n'a cadrées.
2. **Où vivent les specs ?** Dans une branche locale, elles ne servent à personne. En PR, elles
   deviennent un objet de revue — et c'est là que SDD prend sa valeur d'équipe.
3. **Qui relit le code généré ?** Le volume produit dépasse la capacité de revue humaine. Sans
   revue automatisée en première passe, la revue humaine devient un tampon.

Ce palier répond aux trois. **Dans l'ordre inverse de l'intuition** : on commence par le
levier qui ne demande rien à personne.

---

## 5.1 — Commencer par Copilot code review · ~1 h 30

**Le levier le plus sous-estimé, et le meilleur point d'entrée d'un coaching.**

La raison est politique autant que technique : la code review pilotée par instructions
**apporte de la valeur sans rien demander à l'équipe**. Personne n'a à changer sa façon de
travailler, personne n'a à apprendre une commande. Les PR reçoivent simplement des
commentaires plus pertinents.

Comparé à « à partir de lundi, on écrit des specs avant de coder », c'est incomparablement plus
facile à faire accepter. Et une fois que l'équipe constate que la review connaît les
conventions maison, la question « d'où sort-elle ça ? » ouvre naturellement la conversation sur
les fichiers d'instructions — donc sur SDD. **On tire le fil au lieu de pousser la méthode.**

### La mécanique

Copilot code review honore `.github/copilot-instructions.md` et les instructions ciblées
`.github/instructions/*.instructions.md` avec leur `applyTo`.

```markdown
---
applyTo: "src/main/java/**/interface/**/*.java"
---

# Revue des contrôleurs — contrat RealWorld

Signaler comme défaut, pas comme suggestion :
- Tout `Bearer` dans un en-tête d'authentification. Le contrat impose `Token`.
- Toute erreur de validation qui n'est pas un 422 avec `{"errors":{...}}`.
- Tout endpoint de LISTE d'articles qui renvoie le champ `body` (règle R-7 du PRD).
- Tout calcul de `following` / `favorited` qui ne gère pas le cas anonyme (doit valoir false).
```

**Exercice 5.1** — Écrire trois fichiers d'instructions ciblés (contrôleurs, domaine, tests).
Ouvrir une PR contenant **délibérément** une violation de chacun. Mesurer : combien des trois
sont attrapées ?

Ce chiffre est un excellent matériau d'atelier. S'il est de 3/3, la démonstration se fait
seule. S'il est de 1/3, c'est encore mieux pour le coaching : **ça montre qu'une instruction
doit être écrite comme une règle vérifiable et non comme un souhait**, et on peut réécrire les
deux qui ont échoué en direct, devant l'équipe, jusqu'à ce qu'elles passent.

---

## 5.2 — De `tasks.md` aux issues · ~1 h

```
/speckit.taskstoissues
```

Cette commande convertit le `tasks.md` d'une feature en issues GitHub. C'est la jonction entre
l'artefact SpecKit et le workflow de l'équipe.

**À vérifier sur les issues produites** :
- Chaque issue est-elle **autoportante** ? Un agent qui n'a que le titre et le corps peut-il
  travailler, ou lui manque-t-il le contexte de la spec ?
- Le lien vers `specs/NNN-slug/spec.md` est-il présent ? Sinon, l'ajouter — c'est ce qui rend
  la traçabilité réelle.
- Les dépendances entre tâches sont-elles exprimées ?

> **Le point de vigilance** : une issue générée qui ne référence pas sa spec rompt la chaîne de
> traçabilité, et SDD perd exactement ce qui le distingue d'un backlog ordinaire. Si la commande
> ne pose pas le lien, corriger le template dans `.specify/templates/` plutôt que de le rajouter
> à la main à chaque fois. **Corriger le gabarit, pas l'instance** — c'est un réflexe à
> transmettre : il vaut pour les issues comme pour tout le reste du dispositif.

---

## 5.3 — Le cloud agent, et ses limites dures · ~2 h

On assigne une issue à Copilot ; il travaille sur une branche et ouvre une PR.

### Les limites à connaître avant de promettre quoi que ce soit

| Limite | Conséquence pratique |
|---|---|
| **59 minutes**, non extensible | Une tâche trop grosse échoue **sans livrer**. Le découpage de `tasks.md` doit intégrer cette contrainte en amont. |
| **Un dépôt, une branche** par session | Pas de changement transverse multi-dépôts. Bloquant en architecture microservices. |
| **Dépôts GitHub uniquement** | Sans objet ici, mais rédhibitoire pour une équipe sur GitLab ou Bitbucket. |
| **Rulesets et branch protections** peuvent le bloquer | À tester **avant** de le présenter à l'équipe : une démo qui échoue sur une règle d'organisation coûte cher en crédibilité. |
| **Plan payant + policy admin** | Le blocage est administratif, pas technique. À lever en amont. |

> **La limite de 59 minutes est la contrainte qui remonte le plus haut dans la chaîne.** Elle ne
> se traite pas au moment de l'assignation : elle se traite au moment de `/speckit.tasks`, en
> exigeant des tâches suffisamment fines. C'est une belle illustration du principe SDD — un
> problème d'exécution se corrige en amont, dans l'artefact qui l'a produit.

### L'exercice

Prendre trois issues issues de l'itération 2, de tailles nettement différentes (petite, moyenne,
délibérément trop grosse). Les assigner. Observer :

- Laquelle échoue, et comment échoue-t-elle ? En silence ou avec un diagnostic ?
- La PR produite respecte-t-elle la constitution du projet ?
- L'agent a-t-il lu `AGENTS.md` et `.github/copilot-instructions.md` ? **Le vérifier en
  cherchant une trace observable** — par exemple `Token` et non `Bearer`. C'est la seule preuve
  fiable que le contexte a été consommé.
- Combien de tours de revue avant que la PR soit mergeable ?

**Consigner dans [`journal.md`](journal.md)** : la taille de tâche au-delà de laquelle le cloud
agent devient peu fiable. Ce seuil est une donnée locale, dépendante de la stack et du dépôt.
**C'est exactement le genre de chiffre qu'une équipe attend de son coach** — et qu'aucune
documentation ne peut lui donner.

---

## 5.4 — La CI de conformité · ~1 h 30

Le dernier maillon : automatiser le juge externe du [palier 4](04-chantier-conduit.md).

```yaml
# .github/workflows/conformance.yml
#
# La suite Hurl de RealWorld est le juge du contrat : elle n'est écrite ni par nous
# ni par l'agent. Elle tourne sur CHAQUE PR, y compris celles ouvertes par le cloud agent.
# C'est le garde-fou qui empêche une PR générée de passer sur la seule foi de ses
# propres tests.
name: Conformité RealWorld

on:
  pull_request:
  push:
    branches: [main]

jobs:
  hurl:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready --health-interval 10s
          --health-timeout 5s --health-retries 5
        ports: ['5432:5432']
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: temurin
          cache: maven

      - name: Démarrer l'application
        run: ./mvnw spring-boot:run &
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/postgres

      - name: Attendre que l'application réponde
        run: |
          for i in $(seq 1 60); do
            curl -sf http://localhost:8080/api/tags && exit 0
            sleep 2
          done
          echo "L'application n'a pas démarré dans le délai imparti" >&2
          exit 1

      - name: Installer Hurl
        run: |
          curl -sL -o hurl.deb \
            https://github.com/Orange-OpenSource/hurl/releases/latest/download/hurl_amd64.deb
          sudo dpkg -i hurl.deb

      - name: Suite de conformité RealWorld
        run: HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
```

> **La règle du palier 5 : la CI de conformité est bloquante avant que le cloud agent soit
> activé.** Dans cet ordre, et pas l'inverse. Un agent qui ouvre des PR sans juge automatique
> déplace simplement la charge de vérification sur les humains — c'est-à-dire qu'il crée du
> travail au lieu d'en enlever, et l'équipe le ressentira exactement comme ça.

---

## 5.5 — La chaîne complète

Une fois les quatre briques posées :

```
PRD  →  /speckit.specify   →  spec.md          →  PR de spec, relue par un humain   ← GATE
                                   ↓
        /speckit.clarify   →  spec.md enrichie
                                   ↓
        /speckit.plan      →  plan.md, contracts/
                                   ↓
        /speckit.tasks     →  tasks.md
                                   ↓
        /speckit.taskstoissues  →  issues GitHub
                                   ↓
        assignation au cloud agent  →  PR de code
                                   ↓
        Copilot code review (piloté par .instructions.md)
                                   ↓
        CI : lint + tests + CONFORMITÉ HURL                                          ← GATE
                                   ↓
        revue humaine  →  merge
```

**Les deux gates marqués sont ceux que SpecKit ne fournit pas.** Le premier est social (une PR
de spec relue avant `/speckit.plan`), le second est technique (la CI de conformité). Ce sont
les deux ajouts qui transforment un outil individuel en dispositif d'équipe — et ce sont, comme
vu au [palier 2](02-methode-sdd.md), exactement ce que le pipeline maison de `conduit-fullstack`
avait déjà.

**Exercice 5.5** — Faire tourner la chaîne complète, de bout en bout, sur une tâche de
l'itération 2. Chronométrer chaque étape. Le tableau des durées est un support d'atelier
directement réutilisable au palier 6 : il répond concrètement à la question « combien de temps
ça prend, cette méthode ? ».

---

## Critère de sortie — récapitulatif

- [ ] Trois fichiers `.instructions.md` ciblés, avec le taux de détection mesuré sur une PR
      volontairement fautive.
- [ ] `tasks.md` converti en issues, avec le lien vers la spec vérifié (et le gabarit corrigé
      si besoin).
- [ ] Trois issues de tailles différentes assignées au cloud agent ; le seuil de fiabilité est
      noté.
- [ ] La CI de conformité Hurl tourne sur chaque PR et **bloque**.
- [ ] La chaîne complète a tourné une fois, chronométrée.

→ Palier suivant : [`06-kit-coaching.md`](06-kit-coaching.md)

---

## Sources

- [À propos du cloud agent Copilot](https://docs.github.com/en/copilot/concepts/agents/coding-agent/about-coding-agent)
- [Instructions de dépôt pour Copilot](https://docs.github.com/en/copilot/how-tos/configure-custom-instructions/add-repository-instructions)
- [Suite de conformité RealWorld](https://github.com/gothinkster/realworld/tree/main/specs/api)
- [hurl.dev](https://hurl.dev)
