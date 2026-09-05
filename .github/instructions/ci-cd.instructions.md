---
applyTo: ".github/workflows/**, .github/**/*.yml, .github/**/*.yaml, Dockerfile*, docker-compose.yml, lefthook.yml"
---

# CI/CD et contrôles de livraison

Toute validation automatisée doit exécuter les mêmes contrôles essentiels qu'un contributeur peut reproduire localement : structure des artefacts, syntaxe des scripts, validité des données structurées, liens et tests disponibles.

Un workflow doit échouer explicitement lorsque son prérequis ou son contrôle principal échoue. Ne pas masquer une erreur par un `|| true`, un filtre qui supprime le code de sortie ou une étape placée uniquement dans un chemin qui ne couvre pas ses dépendances.

Les actions et dépendances externes doivent être épinglées selon la convention du dépôt. Les secrets de CI sont injectés par l'environnement et ne sont jamais écrits dans les logs, les fixtures ou les artefacts publiés.

Un résultat local vert ne suffit pas à déclarer la CI verte : après un push, attendre la conclusion du run distant et traiter ses conditions propres, notamment les caches absents et les artefacts générés.
