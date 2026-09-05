---
applyTo: "**/domain/**/*.java, **/application/**/*.java, **/infrastructure/**/*.java, **/interface/**/*.java, **/controller/**/*.java"
---

# Architecture hexagonale et clean architecture

Cette instruction s'applique au terrain Java/Spring de Conduit lorsqu'il existe ; elle ne transforme pas les documents Spec Kit en application runtime.

Les dépendances pointent vers l'intérieur : `domain` ne dépend d'aucun framework, `application` dépend des ports du domaine, `infrastructure` implémente les adaptateurs et `interface` expose les contrôleurs. Le domaine ne connaît ni Spring, ni JPA, ni la base de données.

Les ports sont définis du côté du domaine ou de l'application selon le contrat qu'ils servent. Les cas d'utilisation ne dépendent pas des contrôleurs. Les contrôleurs valident et mappent l'entrée, délèguent, puis mappent la sortie ; ils ne contiennent pas de logique métier.

Les entités, value objects et agrégats protègent leurs invariants. Les écritures entre bounded contexts passent par des contrats ou événements explicites, pas par des imports directs qui créent un couplage caché.
