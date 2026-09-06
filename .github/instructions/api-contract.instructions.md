---
applyTo: "**/interfaces/**/*.java, **/controller/**/*.java, specs/**/contracts/**, docs/prd/specifications/backend/**"
---

# Contrats API Conduit

Cette instruction s'applique aux contrats et au terrain d'implémentation Conduit. Respecter la spec RealWorld et les contrats documentés dans `docs/prd/specifications/backend/`.

L'authentification porte le JWT avec `Authorization: Token <jwt>`, jamais `Bearer`. Les erreurs de validation utilisent `422` et la forme `{"errors":{"champ":["message"]}}`. Les listes d'articles ne renvoient pas le champ `body` lorsque le contrat l'exclut. Pour un visiteur anonyme, `following` et `favorited` valent `false`.

Un contrôleur valide et mappe l'entrée puis délègue au cas d'utilisation ; il ne porte pas la logique métier. Toute divergence avec le contrat doit être documentée et testée.
