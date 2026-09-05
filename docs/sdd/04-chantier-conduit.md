---
title: "Palier 4 — Le chantier Conduit"
description: "Trois itérations SDD complètes sur auth + articles en Java/Spring Boot, jugées par la suite de conformité Hurl officielle de RealWorld."
date: 2026-09-05
status: ACTIVE
effort: "~16 h"
---

# Palier 4 — Le chantier Conduit

> **Le cœur du programme.** Trois itérations sur un périmètre réduit, avec un juge externe.
>
> **Critère de sortie** : `HOST=http://localhost:8080/api ./run-api-tests-hurl.sh` au vert sur
> le périmètre couvert.

---

## 4.0 — Le dispositif, et pourquoi il est construit ainsi

### Le périmètre

Extrait du [PRD](../prd/PRD-conduit.md), section 4 :

| Itération | Features | Endpoints |
|---|---|---|
| **1** — `001-auth-jwt` | F-AUTH-1..4 | `POST /users`, `POST /users/login`, `GET /user`, `PUT /user` |
| **2** — `002-articles-crud` | F-ART-3,4,5,6 | `GET/POST/PUT/DELETE /articles[/:slug]` |
| **3** — `003-articles-listing` | F-ART-1 | `GET /articles` avec `tag`, `author`, `favorited`, `limit`, `offset` |

Neuf endpoints. Ni les commentaires, ni les favoris, ni les profils, ni le feed. **C'est
volontairement petit** — et c'est le point le plus important de la conception du palier.

> **Pourquoi si petit.** L'apprentissage vient de la **répétition du cycle**, pas du volume
> livré. Trois itérations complètes sur neuf endpoints enseignent trois fois plus qu'une
> itération sur vingt-sept : chaque bouclage montre ce que l'itération précédente a raté, et
> laisse l'occasion de corriger. Une seule grosse itération ne montre ses erreurs de méthode
> qu'à la fin, quand il est trop tard pour en tirer autre chose que de la frustration.
>
> Une fois la méthode acquise, étendre au reste du PRD est du travail, plus de
> l'apprentissage.

### Le juge externe — le point non négociable

```bash
git clone --depth 1 https://github.com/gothinkster/realworld.git /tmp/realworld
cp -r /tmp/realworld/specs/api ./conformance/
```

La [suite Hurl](https://github.com/gothinkster/realworld/tree/main/specs/api) est écrite par
RealWorld. Ni toi, ni Copilot, ni SpecKit n'y touchez.

**Pourquoi c'est structurant, et pas un détail de confort.** Un agent qui écrit le code *et*
ses tests produit des tests qui passent — ils encodent ce que le code fait, pas ce qu'il
devrait faire. Le symptôme est traître : la suite est verte, la couverture est bonne, et
l'API est fausse. Un juge écrit par un tiers, avant que ton code existe, est la seule façon
de mesurer la conformité au lieu de la cohérence interne.

C'est aussi **l'argument de coaching le plus fort de tout le programme**. À un développeur qui
dit « Copilot écrit mes tests, je suis couvert », on ne répond pas par un principe. On lui
montre une suite verte à 100 % qui échoue sur Hurl. Cette démonstration se prépare ici.

### La règle du chantier

> **Aucune ligne de code écrite à la main pendant l'implémentation.** Quand le résultat est
> faux, on corrige **la spec, le plan ou la constitution**, et on régénère. On ne patche pas le
> code.

C'est artificiel — en production, on patcherait. Mais c'est le seul régime qui apprend *où* se
situe réellement la cause d'un défaut. Chaque patch manuel masque un défaut d'intention et
supprime l'information qu'on venait chercher.

**Tenir un compteur dans [`journal.md`](journal.md)** : pour chaque défaut, la cause était-elle
dans la spec, le plan, la constitution ou l'agent ? **La distribution de ces causes, à la fin
des trois itérations, est le contenu le plus précieux du programme** — c'est ce qui permet de
dire à une équipe « voilà où ça casse réellement », avec des chiffres tirés du terrain plutôt
qu'une intuition.

---

## 4.1 — La constitution

C'est le fichier le plus important du chantier. Il contraint **toutes** les générations
suivantes.

```
/speckit.constitution
```

### Ce qu'il faut y mettre

Le guide brownfield de SpecKit est explicite : *« ne pas inventer de standards juste pour
remplir le gabarit »* et *« des règles irréalistes créent du bruit plutôt que des contraintes
utiles »*.

Un principe mérite sa place s'il satisfait les trois conditions :
1. il est **vérifiable** — on peut dire si une PR le respecte ;
2. il **corrige un penchant par défaut** de l'agent ;
3. on est **prêt à refuser du code** qui le viole.

Le troisième critère est le filtre le plus sévère, et c'est celui qui manque partout. Un
principe qu'on ne fera jamais respecter dégrade tous les autres : il apprend au lecteur — humain
ou agent — que cette liste est indicative.

### Proposition pour Conduit / Java · Spring Boot

```markdown
## I. Le contrat RealWorld prime sur tout
Le PRD (docs/prd/PRD-conduit.md) et la spec RealWorld sont la source de vérité.
En cas de désaccord entre une préférence d'implémentation et le contrat, le contrat gagne.

Invariants systématiquement mal devinés, à respecter à la lettre :
- En-tête : `Authorization: Token <jwt>` — le préfixe est `Token`, PAS `Bearer`.
- Erreurs de validation : HTTP 422, corps `{"errors":{"champ":["message"]}}`.
- Les endpoints de LISTE d'articles ne renvoient pas le champ `body` (règle R-7).
- `following` et `favorited` valent `false` pour un utilisateur non authentifié.
- `Content-Type: application/json; charset=utf-8`.

## II. Le domaine est isolé du framework
Aucune annotation Spring ni JPA sous `domain/`. Pas de `@Entity`, `@Service`, `@Autowired`.
Le domaine est du Java simple, testable sans contexte Spring.
La persistance et le mapping vivent dans `infrastructure/`.

## III. Tout endpoint a un test d'intégration HTTP réel
Un test qui traverse la couche HTTP (MockMvc ou WebTestClient), pas seulement le service.
Un test qui ne prouve que son propre montage est refusé.

## IV. Les secrets ne sont jamais en dur
Secret JWT, URL de base de données : par configuration externe. La configuration échoue
au démarrage (fail-fast) si une valeur requise manque, sans réafficher sa valeur.

## V. Les mots de passe sont hashés
Argon2id ou BCrypt. Le champ `password` n'apparaît dans aucune réponse, aucun log,
aucun message d'erreur.
```

**Exercice 4.1** (1 h) — Écrire la constitution. Puis la **passer au filtre des trois
critères**, principe par principe, et supprimer ceux qui échouent. Consigner ce qui a été
supprimé et pourquoi : cet exercice de suppression est un excellent atelier d'équipe, parce
qu'il force à distinguer ce qu'on croit exiger de ce qu'on exige vraiment.

---

## 4.2 — Itération 1 : `001-auth-jwt` · ~5 h

### Le déroulé

```
/speckit.specify

Périmètre : authentification et gestion de l'utilisateur courant de Conduit.
Référence fonctionnelle : docs/prd/PRD-conduit.md sections 7.1, 8, 9, 10, et les
règles R-8 (unicité email/username) et R-9 (mot de passe jamais renvoyé, stocké hashé).

Un visiteur peut créer un compte et se connecter. Un membre authentifié peut consulter
et modifier son compte. Les réponses respectent le format `User` de la section 8.

Hors périmètre : profils publics, suivi, articles, commentaires.
```

Puis **impérativement** :

```
/speckit.clarify
```

### Ce qu'il faut observer, étape par étape

| Étape | Ce qu'il faut faire, et pas seulement lancer |
|---|---|
| `specify` | Relire `spec.md`. **Traquer les fuites techniques** : si elle nomme Spring Security, JPA ou une classe, la corriger à la main avant d'aller plus loin. Le [test du palier 2](02-methode-sdd.md) s'applique : la spec survit-elle à une réécriture en Go ? |
| `clarify` | **Répondre sérieusement.** Attendre des questions sur : la longueur minimale du mot de passe, la durée de validité du JWT, le comportement sur email déjà pris, ce que renvoie `PUT /user` avec un corps vide. Chaque question non posée = une hypothèse silencieuse. |
| `plan` | Vérifier que chaque choix est **justifié**. « On utilise Spring Security » sans motif est un signal faible : le plan récite au lieu de décider. |
| `tasks` | Estimer la plus grosse tâche. Si elle dépasse ~45 min de travail agent, la découper **maintenant** — la limite de 59 min du cloud agent arrive au palier 5. |
| `analyze` | Noter s'il trouve de vraies incohérences ou produit un rapport de complaisance. |
| `implement` | Ne rien corriger à la main. Noter chaque défaut et sa cause. |
| `converge` | Rouvre-t-il du travail réel ? |

### Le verdict

```bash
./mvnw spring-boot:run &
HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
```

**Le premier passage échouera.** C'est attendu, et c'est le moment le plus instructif du
programme.

**Le protocole face à un échec** — dans cet ordre, sans le raccourcir :

1. Lire l'échec Hurl : quel endpoint, quel écart exact ?
2. **Remonter à la cause** : est-ce que `spec.md` couvrait ce cas ? Si non → la spec était
   incomplète. Si oui → le plan ou l'implémentation a dévié.
3. Corriger **à la source** : la spec, ou la constitution si l'invariant vaut pour tout le
   projet.
4. Régénérer.
5. Noter dans `journal.md` : le symptôme, la cause, l'étape corrigée.

> **Le pari du protocole.** Les échecs récurrents remonteront presque tous à la constitution
> (`Bearer` au lieu de `Token`, format d'erreur, `body` dans les listes). Chaque correction
> portée dans la constitution profite **aux itérations 2 et 3**, alors qu'un patch dans le code
> ne profite à rien. La courbe d'échecs entre les itérations 1, 2 et 3 est la démonstration
> chiffrée que le coaching cherche. **Relever les trois chiffres.**

---

## 4.3 — Itération 2 : `002-articles-crud` · ~5 h

Même cycle. Trois nouveautés à observer.

**Le contexte accumulé.** La constitution est enrichie des corrections de l'itération 1. Le
plan a un existant sur lequel s'appuyer. **La question à mesurer** : y a-t-il moins d'échecs
Hurl qu'à l'itération 1 ? De combien ? C'est le chiffre qui prouve — ou réfute — que
l'investissement en spec se rentabilise.

**La génération du slug (R-1).** Le PRD dit que le slug vient du titre en kebab-case. Il ne dit
pas ce qui se passe sur collision de titres. **La spec doit trancher** — et si `/speckit.clarify`
ne pose pas la question, c'est une information à noter : elle marque la limite de ce que
l'outil détecte tout seul, et c'est exactement ce qu'un coach doit savoir avant de promettre
que « l'outil pose les bonnes questions ».

**L'autorisation (R-6).** Seul l'auteur peut éditer ou supprimer son article — sinon 403. C'est
la première règle de sécurité du chantier. Vérifier que la spec l'exprime comme un
**comportement observable** (« un membre qui n'est pas l'auteur reçoit 403 ») et non comme une
implémentation (« le service vérifie `article.author.id == currentUser.id` »).

> **Le piège d'autorisation à repérer** : sur un article inexistant, faut-il renvoyer 404 ou
> 403 ? Le PRD ne le dit pas. Un 403 sur une ressource absente **révèle son existence** — c'est
> une fuite d'information par canal auxiliaire. Si l'agent ne soulève pas la question, c'est un
> excellent cas d'école pour le playbook : **une spec incomplète sur un point de sécurité produit
> un défaut de sécurité, pas une erreur de compilation.** Rien ne la signalera.

---

## 4.4 — Itération 3 : `003-articles-listing` · ~6 h

**L'itération la plus importante du programme. Ne pas la sauter.**

Les deux premières se sont bien passées : Conduit est un domaine simple, SDD y brille. Celle-ci
est conçue pour **faire mal**, parce que c'est là que se trouvent les arguments d'un coach
crédible.

`GET /articles` accepte `tag`, `author`, `favorited`, `limit`, `offset`. Le PRD (règle R-3) dit
seulement qu'il accepte « au plus un usage cohérent des filtres ». **C'est une spec floue, et
elle est floue dans le document de référence lui-même.**

Les questions que la spec doit trancher et que le PRD laisse ouvertes :

| Question | Pourquoi c'est piégeux |
|---|---|
| Les filtres se combinent-ils en ET ou en OU ? | Change complètement le résultat |
| `favorited=inconnu` : liste vide ou 404 ? | Deux comportements également défendables |
| `limit=0` ? `limit=10000` ? | Absence de borne = risque de déni de service |
| `offset` négatif ? | Comportement indéfini |
| `tag` inexistant : liste vide ou erreur ? | Sémantique de filtre vs sémantique de recherche |
| Tri stable si deux articles ont le même `createdAt` ? | Pagination incohérente entre deux pages |

**Le protocole de l'itération 3** — deux passes, délibérément :

**Passe A — sous-spécifier volontairement.** Écrire la spec sans trancher ces questions.
Laisser `/speckit.clarify` faire ce qu'il peut. Aller jusqu'à Hurl. **Compter les échecs et les
classer** : lesquels viennent d'un flou de spec plutôt que d'une erreur d'implémentation ?

**Passe B — trancher les six questions**, reprendre le cycle, remesurer.

> **Le livrable de coaching de cette itération** : l'écart chiffré entre A et B. C'est la
> réponse la plus solide à l'objection « on perd du temps à écrire des specs » — parce qu'elle
> ne repose pas sur une conviction mais sur deux mesures faites dans les mêmes conditions, sur
> le même code, avec le même juge. Un coach qui a ce chiffre en poche n'a plus besoin de
> convaincre.
>
> C'est aussi le seul endroit du programme où l'on observe **le tri stable** : sans lui, la
> pagination renvoie des doublons entre deux pages. Le défaut est invisible en test unitaire,
> visible en test de conformité. Bon matériau d'atelier.

---

## 4.5 — La synthèse du palier

À produire dans `journal.md`, ce sont les données du palier 6 :

| Mesure | It. 1 | It. 2 | It. 3-A | It. 3-B |
|---|---|---|---|---|
| Échecs Hurl au 1er passage | | | | |
| Cycles de régénération jusqu'au vert | | | | |
| Défauts dus à la **spec** | | | | |
| Défauts dus au **plan** | | | | |
| Défauts dus à la **constitution** | | | | |
| Défauts dus à l'**agent** (spec juste, code faux) | | | | |
| Questions posées par `/speckit.clarify` | | | | |
| Vrais problèmes trouvés par `/speckit.analyze` | | | | |

Et trois questions à réponse écrite :

1. **Quelle proportion des défauts venait de l'intention** (spec + plan + constitution) plutôt
   que de l'agent ? C'est le chiffre qui justifie SDD — ou qui le relativise. Le publier tel
   qu'il sort, même s'il déplaît.
2. **`/speckit.analyze` et `/speckit.converge` sont-ils des gates fiables**, ou faut-il un juge
   externe ? Répondre avec les observations, pas avec l'intuition.
3. **Quelles corrections portées dans la constitution ont profité aux itérations suivantes ?**
   C'est la démonstration concrète du retour sur investissement d'un contexte bien écrit.

---

## Critère de sortie — récapitulatif

- [ ] Constitution écrite, passée au filtre des trois critères, avec les suppressions tracées.
- [ ] Les trois itérations bouclées, y compris les deux passes de l'itération 3.
- [ ] Suite Hurl au vert sur les neuf endpoints.
- [ ] Aucune ligne de code écrite à la main pendant les implémentations.
- [ ] Le tableau de synthèse est rempli.
- [ ] Les trois questions ont une réponse écrite.

→ Palier suivant : [`05-industrialisation.md`](05-industrialisation.md)

---

## Sources

- [PRD Conduit](../prd/PRD-conduit.md) · [Spécifications RealWorld locales](../prd/specifications/README.md)
- [Suite de conformité API RealWorld (Hurl)](https://github.com/gothinkster/realworld/tree/main/specs/api)
- [OpenAPI officiel Conduit](../prd/specifications/backend/openapi.yml)
- [hurl.dev](https://hurl.dev)
