---
title: "PRD - Conduit (RealWorld), reference domain"
description: "Product Requirements Complete document of the Conduit app (spec RealWorld): vision, functional scope, data model, exhaustive API specification, JWT auth, error management, business rules, acceptance criteria."
date: 2026-06-04
status: REFERENCE
source: "https://realworld-docs.netlify.app/ (spec RealWorld officielle)"
---

# PRD - Conduit (l'app RealWorld)

> Reference business domain: the **Conduit** app (spec RealWorld), implemented here in full-stack TypeScript. This PRD is the functional source of truth for the repository - code comments refer to it by section (`PRD §7`, `§8`, etc.).
>
> RealWorld specifications locally (Markdown): [`./specifications/`](./specifications/).

---

## 1. Context and objective

**Conduit** is the reference application of the [RealWorld](https://realworld-docs.netlify.app/) project: a clone of Medium (social blogging). Its educational interest: the business domain is **fixed and known to all developers**, so the reader of a repository concentrates 100% on the architectural technique, never on the business.

This PRD is used to:
- Freeze the functional scope (no scope drift).
- Serve as a basis for the repository's REQ-as-code (`docs/requirements/`).
- Guarantee that the implementation remains faithful to the official RealWorld contract (compliance subsequently verified Hurl).

**Guiding principle**: we do not reinvent Conduit. We use the RealWorld spec identically. The value is in the **how** (architecture and craftsmanship), not in the **what**.

---

## 2. Vision produit

Conduit is a community article publishing platform. A user can:
- register, log in, manage your profile;
- publish, edit, delete articles in Markdown, tagged;
- read a global feed or a personalized feed (authors it follows);
- follow/unfollow other authors;
- add to favorites / remove from favorites;
- comment on articles and delete your own comments;
- filter articles by tag.

---

## 3. Personas

| Persona | Description | Abilities |
|---|---|---|
| **Visiteur anonyme** | Unauthenticated | Read the overall feed, read an article, read profiles, read comments, list tags |
| **Authenticated member** | Compte + JWT | All of the above + publish/edit/delete your articles, follow, favor, comment, personal feed, edit your profile |
| **Auteur** | Member, owner of an article or comment | Edit/delete **their** articles, delete **their** comments |

> Transversal rule: the fields `following` (profile) and `favorited` (article) are calculated **relative to the current user**. To an anonymous visitor, they are still worth `false`.

---

## 4. Functional scope (MoSCoW)

### Must

| ID | Functionality |
|---|---|
| F-AUTH-1 | Registration (email, username, password) |
| F-AUTH-2 | Connection (email, password) → returns a JWT |
| F-AUTH-3 | Retrieve the current user (via JWT) |
| F-AUTH-4 | Update current user (email, username, password, bio, image) |
| F-PROF-1 | View a public profile |
| F-PROF-2 | Follow/unfollow a user |
| F-ART-1 | List articles (tag / author / favorited filters, pagination) |
| F-ART-2 | Personal feed (articles from authors followed) |
| F-ART-3 | View an article by slug |
| F-ART-4 | Create an article (title, description, body, tagList) |
| F-ART-5 | Edit an article (author only) |
| F-ART-6 | Delete an article (author only) |
| F-CMT-1 | Add a comment |
| F-CMT-2 | List comments on an article |
| F-CMT-3 | Delete a comment (author only) |
| F-FAV-1 | Favor/unfavorite an article |
| F-TAG-1 | List tags |

### Should

| ID | Functionality |
|---|---|
| F-UI-1 | Client-side Markdown rendering of the body |
| F-UI-2 | Contextual header (authenticated vs anonymous) |
| F-UI-3 | Sidebar "Popular Tags" |

### Could / Won't (outside scope)

- No messaging, no notifications, no full text search, no image upload (image is a URL), no roles/admin.

---

## 5. Parcours et routes frontend

> Routing in hash (`/#/...`) in the reference spec. JWT stored as `localStorage`.

| Route | Page | Auth |
|---|---|---|
| `/#/` | Home: list of tags, articles (global feed / personal / by tag), pagination | Optionnel |
| `/#/login` | Connection | Anonyme |
| `/#/register` | Registration | Anonyme |
| `/#/settings` | Account settings + logout | Requis |
| `/#/editor` | Create an article | Requis |
| `/#/editor/:slug` | Edit an article | Requis (auteur) |
| `/#/article/:slug` | Article: Markdown body, comments, delete button (author) | Optionnel |
| `/#/profile/:username` | User profile + articles | Optionnel |
| `/#/profile/:username/favorites` | User Favorite Posts | Optionnel |

> The BFF (repository #3) also introduces a mobile client: the PRD frontend remains the reference for needs, the BFF adapts the responses per client.

---

## 6. Data Model

```
User 1 ──< Article >── many Tag        (un article a une tagList)
User 1 ──< Comment >── 1 Article
User many ──< Follow >── many User      (relation de suivi)
User many ──< Favorite >── many Article (relation de favori)
```

### User (private entity)

| Champ | Type | Notes |
|---|---|---|
| email | string | unique, requis |
| username | string | unique, requis |
| password | string | required, **stored hashed**, never returned |
| bio | string | nullable |
| image | string (URL) | nullable, default avatar possible |
| token | string (JWT) | returned in User responses only |

### Profile (vue publique de User)

| Champ | Type | Notes |
|---|---|---|
| username | string |                                |
| bio | string | nullable |
| image | string (URL) |                                |
| following | bool | relative to the current user |

### Article

| Champ | Type | Notes |
|---|---|---|
| slug | string | **generated from title** (kebab-case), public identifier |
| title | string | requis |
| description | string | requis |
| body | string (Markdown) | required ; **not returned in the lists** (see rule R-7) |
| tagList | string[] |                                |
| createdAt | datetime ISO 8601 |                                |
| updatedAt | datetime ISO 8601 |                                |
| favorited | bool | relative to the current user |
| favoritesCount | int |                                |
| author | Profile |                                |

### How

| Champ | Type | Notes |
|---|---|---|
| id | int |                                |
| body | string | requis |
| createdAt | datetime ISO 8601 |                                |
| updatedAt | datetime ISO 8601 |                                |
| author | Profile |                                |

### Tag

Simple chain. No rich entity.

---

## 7. API specification (exhaustive)

Base URL: `/api`. Authentication header: `Authorization: Token jwt.token.here`. Content-Type of responses: `application/json; charset=utf-8`.

### 7.1 Authentication and user

| Action | Method | Path | Auth | Champs requis |
|---|---|---|---|---|
| Connection | POST | `/api/users/login` | Non | email, password |
| Registration | POST | `/api/users` | Non | username, email, password |
| Current user | GET | `/api/user` | Oui | - |
| Update | PUT | `/api/user` | Oui | (all optional: email, username, password, image, bio) |

```jsonc
// POST /api/users/login
{"user":{"email":"jake@jake.jake","password":"jakejake"}}

// POST /api/users
{"user":{"username":"Jacob","email":"jake@jake.jake","password":"jakejake"}}

// PUT /api/user
{"user":{"email":"jake@jake.jake","bio":"I like to skateboard","image":"https://i.stack.imgur.com/xHWG8.jpg"}}
```

### 7.2 Profils

| Action | Method | Path | Auth |
|---|---|---|---|
| Consulter | GET | `/api/profiles/:username` | Optionnel |
| Suivre | POST | `/api/profiles/:username/follow` | Oui |
| Ne plus suivre | DELETE | `/api/profiles/:username/follow` | Oui |

### 7.3 Articles

| Action | Method | Path | Auth | Query / Body |
|---|---|---|---|---|
| Lister | GET | `/api/articles` | Optionnel | `tag`, `author`, `favorited`, `limit` (def. 20), `offset` (def. 0) |
| Flux personnel | GET | `/api/articles/feed` | Oui | `limit`, `offset` |
| Consulter | GET | `/api/articles/:slug` | Non | - |
| Create | POST | `/api/articles` | Oui | title, description, body (+ tagList) |
| Edit | PUT | `/api/articles/:slug` | Oui (auteur) | title, description, body (optionnels) |
| DELETE | DELETE | `/api/articles/:slug` | Oui (auteur) | - |

```jsonc
// POST /api/articles
{"article":{"title":"How to train your dragon","description":"Ever wonder how?","body":"You have to believe","tagList":["reactjs","angularjs","dragons"]}}

// PUT /api/articles/:slug
{"article":{"title":"Did you train your dragon?"}}
```

### 7.4 Commentaires

| Action | Method | Path | Auth |
|---|---|---|---|
| Add | POST | `/api/articles/:slug/comments` | Oui |
| Lister | GET | `/api/articles/:slug/comments` | Optionnel |
| DELETE | DELETE | `/api/articles/:slug/comments/:id` | Oui (auteur) |

```jsonc
// POST /api/articles/:slug/comments
{"comment":{"body":"His name was my name too."}}
```

### 7.5 Favoris et tags

| Action | Method | Path | Auth |
|---|---|---|---|
| Favoriser | POST | `/api/articles/:slug/favorite` | Oui |
| Unfavorite | DELETE | `/api/articles/:slug/favorite` | Oui |
| List tags | GET | `/api/tags` | Non |

---

## 8. Response formats (verbatim)

### User (with token)
```json
{
  "user": {
    "email": "jake@jake.jake",
    "token": "jwt.token.here",
    "username": "jake",
    "bio": "I work at statefarm",
    "image": null
  }
}
```

### Profile
```json
{
  "profile": {
    "username": "jake",
    "bio": "I work at statefarm",
    "image": "https://api.realworld.io/images/smiley-cyrus.jpg",
    "following": false
  }
}
```

### Single Article
```json
{
  "article": {
    "slug": "how-to-train-your-dragon",
    "title": "How to train your dragon",
    "description": "Ever wonder how?",
    "body": "It takes a Jacobian",
    "tagList": ["dragons", "training"],
    "createdAt": "2016-02-18T03:22:56.637Z",
    "updatedAt": "2016-02-18T03:48:35.824Z",
    "favorited": false,
    "favoritesCount": 0,
    "author": {
      "username": "jake",
      "bio": "I work at statefarm",
      "image": "https://i.stack.imgur.com/xHWG8.jpg",
      "following": false
    }
  }
}
```

### Multiple Articles
```json
{
  "articles": [
    {
      "slug": "how-to-train-your-dragon",
      "title": "How to train your dragon",
      "description": "Ever wonder how?",
      "tagList": ["dragons", "training"],
      "createdAt": "2016-02-18T03:22:56.637Z",
      "updatedAt": "2016-02-18T03:48:35.824Z",
      "favorited": false,
      "favoritesCount": 0,
      "author": {
        "username": "jake",
        "bio": "I work at statefarm",
        "image": "https://i.stack.imgur.com/xHWG8.jpg",
        "following": false
      }
    }
  ],
  "articlesCount": 2
}
```

### Single Comment
```json
{
  "comment": {
    "id": 1,
    "createdAt": "2016-02-18T03:22:56.637Z",
    "updatedAt": "2016-02-18T03:22:56.637Z",
    "body": "It takes a Jacobian",
    "author": {
      "username": "jake",
      "bio": "I work at statefarm",
      "image": "https://i.stack.imgur.com/xHWG8.jpg",
      "following": false
    }
  }
}
```

### Multiple Comments
```json
{
  "comments": [
    {
      "id": 1,
      "createdAt": "2016-02-18T03:22:56.637Z",
      "updatedAt": "2016-02-18T03:22:56.637Z",
      "body": "It takes a Jacobian",
      "author": {
        "username": "jake",
        "bio": "I work at statefarm",
        "image": "https://i.stack.imgur.com/xHWG8.jpg",
        "following": false
      }
    }
  ]
}
```

### Tags
```json
{
  "tags": ["reactjs", "angularjs"]
}
```

---

## 9. Authentication

- Mechanism: **JWT**.
- Header of authenticated requests: `Authorization: Token jwt.token.here` (prefix `Token`, not `Bearer`).
- The token is only returned in `User` (login, register, get/update user) responses.
- Reference frontend side: the token is stored in `localStorage`.
- “Optional Auth” endpoints adapt their response according to the presence/validity of the token (calculation of `following` / `favorited`).

---

## 10. Error handling

| Code | Cas |
|---|---|
| 422 Unprocessable Entity | Validation failure |
| 401 Unauthorized | Authentication required but missing/invalid |
| 403 Forbidden | Valid request but missing permission (eg: edit someone else's article) |
| 404 Not Found | Ressource introuvable |

Format of validation errors (verbatim):
```json
{"errors":{"body":["can't be empty"]}}
```
The `errors` key maps field names to message tables.

---

## 11. Business rules

| ID | Ruler |
|---|---|
| R-1 | The `slug` of an article is generated from the `title` (kebab-case) and serves as a public identifier. |
| R-2 | The list of articles is sorted by descending creation date (newest first). |
| R-3 | `GET /api/articles` accepts at most consistent use of the filters `tag`, `author`, `favorited`, combined with `limit`/`offset`. |
| R-4 | The personal feed (`/feed`) only returns articles from authors followed by the current user; it requires authentication. |
| R-5 | `following` and `favorited` are calculated relative to the current user (`false` if anonymous). |
| R-6 | Only the author can edit/delete his article and delete his comment (otherwise 403). |
| R-7 | As of 2024-08-16, item **list** endpoints no longer return `body` (performance). The `body` remains present on the single item. |
| R-8 | `email` et `username` sont uniques. |
| R-9 | The `password` is never returned; it is stored hashed. |
| R-10 | Paging: `limit` default 20, `offset` default 0. |

---

## 12. Exigences non-fonctionnelles

| Domaine | Requirement |
|---|---|
| Format | JSON ; `Content-Type: application/json; charset=utf-8`. |
| Security | Hashed passwords; JWT signed; no sensitive field leakage. |
| Consistency | All 5 repositorys must pass the **same** set of functional tests (spec RealWorld provides an official conformance suite, see section 15). |
| Performance | Paginated lists; no `body` in the lists (R-7). |
| Observability | Selon repository (notamment `conduit-microservices`, phase durcissement). |

> Recommendation: use the **RealWorld conformance suite** (Hurl tests, see section 15) as a common safeguard. A repository is only "Conduit compliant" if it passes it. This is what makes the implementations rigorously comparable.

---

## 13. Acceptance criteria (common to any Conduit repository)

- [ ] All section 7 endpoints respond with section 8 formats.
- [ ] JWT authentication works (`Authorization: Token ...`).
- [ ] Errors comply with section 10 (422 + `errors` format).
- [ ] Business rules R-1 to R-10 are respected.
- [ ] The RealWorld compliance suite (Hurl tests) goes green (see section 15).
- [ ] Relative fields (`following`, `favorited`) are correct for anonymous and authenticated.

---

## 14. Scope per repository (the same Conduit, 5 angles)

| Repo | Implements | Special feature vs this PRD |
|---|---|---|
| `conduit-craft-ai` | Conduit complet, monolithe hexagonal | Implementation reference. Scope v1: auth + articles + comments |
| `conduit-api-first` | Conduit complet | The OpenAPI contract **precedes** the implementation; types are generated |
| `conduit-bff` | Conduit + 2 clients (web, mobile) | Adds one BFF layer per client; the answers are adapted per client |
| `conduit-microservices` | Conduit cut then hardened | Bounded contexts (articles, users, comments) + database-per-service, then circuit breaker / retry / async (resilience) |
| `conduit-fullstack` | Conduit en full-stack TypeScript | Monorepository api + web + shared; shared model, end-to-end safety type (contrast with the Java spine) |

> The **what** (this PRD) never changes. Only the **how** changes from one repository to another. This is the RealWorld bet: fixed domain, variable architecture.

---

## 15. Compliance suite (official tests RealWorld)

> Important: the RealWorld spec **has evolved**. The old Postman/Newman collection is obsolete. The current suite lives in repository `gothinkster/realworld`, folder `specs/`.

### 15.1 Tests API

| Element | Detail |
|---|---|
| Source of truth | Tests **Hurl** (`specs/api/hurl/`), https://hurl.dev |
| Collection alternative | **Bruno** (`specs/api/bruno/`), generated from Hurl, https://www.usebruno.com |
| Contrat | `specs/api/openapi.yml` - **Official OpenAPI** from Conduit (to be reused directly by the `conduit-api-first` repository) |
| Synchro | Bruno regenerated via `make bruno-generate`, verified in CI via `make bruno-check` |

Commandes (verbatim) :
```bash
# Hurl tests (source of truth)
HOST=http://localhost:3000/api ./run-api-tests-hurl.sh

# Bruno tests (generated, équivalent)
HOST=http://localhost:3000/api ./run-api-tests-bruno.sh
```

### 15.2 Tests end-to-end (frontend)

Playwright specifications in `specs/e2e/` (TypeScript), including: `auth.spec.ts`, `articles.spec.ts`, `comments.spec.ts`, `social.spec.ts`, `settings.spec.ts`, An `SELECTORS.md` file documents the expected selectors on the UI side.

### 15.3 Recommended use by repository

| Repo | Compliance suite to aim for |
|---|---|
| `conduit-craft-ai` | Hurl (API) tests green; these are the acceptance tests |
| `conduit-api-first` | Partir de `specs/api/openapi.yml` comme contrat, puis Hurl au vert |
| `conduit-bff` | Hurl on the back; e2e Playwright by customer |
| `conduit-microservices` | Hurl green despite cutting (compliance does not change) + in-house failure scenarios (hardening) |
| `conduit-fullstack` | Hurl green on the API + e2e Playwright on the front |

---

## 16. Sources

> The RealWorld specifications have been repatriated locally (Markdown verbatim) in [`./specifications/`](./specifications/). Work from these files rather than URLs. The links below are the original upstream source.

- Local specifications: [`./specifications/`](./specifications/) (backend, frontend, mobile, tests)
- Official RealWorld spec (upstream): https://realworld-docs.netlify.app/
  - Introconduition: https://realworld-docs.netlify.app/introconduition/
  - Endpoints: https://realworld-docs.netlify.app/specifications/backend/endpoints/
  - Response formats: https://realworld-docs.netlify.app/specifications/backend/api-response-format/
  - Error handling: https://realworld-docs.netlify.app/specifications/backend/error-handling/
  - Frontend routing: https://realworld-docs.netlify.app/specifications/frontend/routing/
  - Features: https://realworld-docs.netlify.app/specifications/frontend/templates/
- Official filing: https://github.com/gothinkster/realworld
  - API compliance suite: https://github.com/gothinkster/realworld/tree/main/specs/api
  - Official OpenAPI: https://github.com/gothinkster/realworld/blob/main/specs/api/openapi.yml
  - e2e Playwright tests: https://github.com/gothinkster/realworld/tree/main/specs/e2e
