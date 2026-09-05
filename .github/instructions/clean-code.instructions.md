---
applyTo: "**/*.java, **/*.ts, **/*.tsx, **/*.sh, **/*.mjs"
---

# Clean code et qualité

Écrire des unités courtes, nommées par intention et limitées à une responsabilité. Une méthode qui dépasse nettement une page, qui mélange orchestration et décision métier, ou qui impose plusieurs niveaux de lecture doit être découpée avant la revue.

Préférer les types et contrats explicites aux casts permissifs, aux valeurs nulles implicites et aux contournements de validation. Ne pas utiliser `any`, une assertion non nulle ou une suppression de diagnostic sans invariant documenté et vérifiable.

Les helpers portent un nom verbal décrivant leur intention. Garder un helper dans son fichier d'origine tant qu'il n'est pas réutilisé. Ajouter une preuve ciblée lorsqu'une extraction contient une branche, une transformation ou un cas limite non trivial.

Les règles de lint et de formatage existantes sont des garde-fous : ne pas les affaiblir pour faire passer un changement. Toute exception doit être locale, justifiée et suivie d'un contrôle qui prouve qu'elle ne masque pas une régression.
