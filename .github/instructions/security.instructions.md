---
applyTo: "**/*.java, **/*.ts, **/*.tsx, **/*.sh, **/*.yml, **/*.yaml, **/*.json, .github/**, .specify/**"
---

# Sécurité by design

Ne jamais committer de secret, token, mot de passe, clé privée, donnée personnelle réelle ou valeur d'environnement sensible. Les exemples utilisent des placeholders manifestement fictifs. Les scripts échouent explicitement sans afficher les secrets reçus.

Les secrets et paramètres sensibles viennent d'une configuration externe validée au démarrage de l'application cible. Un secret manquant ou malformé doit provoquer un échec explicite, pas un mode dégradé silencieux.

Ne jamais faire confiance à une identité, un rôle ou un champ sensible fourni par le client. L'autorité vient d'une session ou d'un token vérifié côté serveur. Les contrôles de propriété doivent faire partie de la requête ou du cas d'utilisation autorisé, pas d'un contrôle oublié après coup.

Toute dépendance, action CI, URL externe ou commande exécutée par un workflow doit être identifiée et, lorsque le mécanisme le permet, épinglée ou validée. Toute décision de sécurité importante est documentée dans un ADR ou dans l'exigence concernée.
