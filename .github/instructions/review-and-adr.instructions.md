---
applyTo: "**/*"
---

# Commits, revue et décisions

Un changement doit être aussi simple et local que possible. Chercher la cause racine, éviter les contournements temporaires et ne pas modifier des fichiers sans lien avec l'intention.

Lorsqu'un commit est demandé, utiliser Conventional Commits et un sujet impératif qui explique le pourquoi lorsque celui-ci n'est pas évident. Ne jamais créer un commit de sa propre initiative.

Toute décision structurante concernant un outil, une architecture, un contrat ou un workflow doit être documentée dans un ADR avant l'implémentation si elle est connue à l'avance. Une décision apparue pendant le travail doit être ajoutée dès qu'elle est identifiée.

Une revue vérifie d'abord les régressions comportementales, les exigences non couvertes, les risques de sécurité, les liens cassés et les preuves manquantes. Formuler les commentaires avec `issue:`, `suggestion:`, `question:` ou `nit:` et distinguer un blocage d'une préférence.
