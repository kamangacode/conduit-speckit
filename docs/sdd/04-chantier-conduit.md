---
title: "Conduit construction site, implementing every feature"
description: "A step-by-step path to turn the Conduit PRD into Java/Spring Boot features verified by Spec Kit and RealWorld conformance."
date: 2026-09-07
status: ACTIVE
audience: "Developer implementing Conduit"
---

# Conduit construction site

> This file is the implementation path. It does not replace the [PRD](../prd/PRD-conduit.md), which is the functional source of truth, or the `specs/NNN-slug/` artifacts, which are the source of feature state. Each step produces a complete, verifiable feature before the next one begins.

## 4.0 Prepare the construction site

Read sections 2, 4, 6, 7, 8, 9, 10, and 11 of the PRD. Verify that Spec Kit is installed as described in [03-speckit.md](03-speckit.md), then prepare the repository and the constitution:

```bash
git switch develop
git pull --ff-only origin develop
/speckit-constitution
```

At minimum, the constitution must state the RealWorld invariants:

- `Authorization: Token <jwt>`, never `Bearer`;
- validation errors use `422` with `{"errors":{"field":["message"]}}`;
- article lists never include `body`;
- `following` and `favorited` are `false` for an anonymous visitor;
- passwords are hashed, never returned or logged;
- the domain is independent from Spring and JPA;
- every endpoint has HTTP-level proof.

Do not add a rule unless it is verifiable and useful for correcting a concrete generation bias.

## 4.1 The mandatory cycle for every feature

For every row in the table below, start from `develop`, create the feature branch shown in the feature section, and run the same cycle:

```text
/speckit-specify
/speckit-clarify
                 <- review and validate spec.md before continuing
/speckit-plan
/speckit-tests
/speckit-tasks
/speckit-analyze
/speckit-implement
/speckit-converge
                 <- local tests, then Hurl and Bruno when covered by the feature
```

At every pass:

1. Reference the PRD `F-*` and `R-*` IDs in `spec.md`.
2. Answer every clarification question, especially HTTP statuses and authorization cases.
3. Check that `plan.md` introduces no behavior absent from `spec.md`.
4. Check that `test-cases.yaml` has an `AC-*` case for every observable behavior.
5. Check that `tasks.md` links every task to an `AC-*`, `FR-*`, `F-*`, or plan decision.
6. When proof fails, correct the intent or plan before correcting the code.
7. Record the executed command and result in `specs/NNN-slug/traceability.md`.

Common proof commands:

```bash
./mvnw test
./mvnw verify
HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
HOST=http://localhost:8080/api ./conformance/run-api-tests-bruno.sh
```

Hurl is the external oracle. Bruno is a derived execution and never becomes a second contract source.

## 4.2 Feature branch and pull request protocol

Each feature has its own branch. Never branch from `main`, from a previous feature branch, or from a branch containing unrelated work. Both `develop` and `main` are protected branches.

At the beginning of a feature, run the command documented in its section. The command must update `develop` first and then create the branch:

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feat/<feature-slug>
```

Run all Spec Kit commands and implementation work on that branch. Before opening the pull request, run the feature's local tests and the applicable Hurl or Bruno checks:

```bash
./mvnw verify
git status --short
git diff develop...HEAD
git push --set-upstream origin feat/<feature-slug>
gh pr create --base develop --head feat/<feature-slug>
```

The pull request must point to `develop`. After the feature has been integrated and the release is ready, changes can flow from protected `develop` to protected `main` according to the repository release process. The feature pull request should include the feature scope, the relevant `specs/NNN-slug/` artifacts, test results, traceability evidence, and any external conformance result.

## 4.3 Complete feature order

| Order | Spec directory | PRD features | Depends on | Exit evidence |
|---:|---|---|---|---|
| 1 | `001-user-authentication` | F-AUTH-1 to F-AUTH-4 | project foundation | registration, login, current user, and update conform |
| 2 | `002-profiles-and-following` | F-PROF-1 and F-PROF-2 | authentication | public profile, follow, and unfollow |
| 3 | `003-articles-crud` | F-ART-3 to F-ART-6 | authentication | read, create, edit, and delete an article |
| 4 | `004-articles-listing` | F-ART-1 | article CRUD | filters, ordering, pagination, and no `body` in lists |
| 5 | `005-favorites` | F-FAV-1 | article CRUD, authentication | favorite/unfavorite, count, and relative fields |
| 6 | `006-personal-feed` | F-ART-2 | profiles/following, articles, favorites | feed limited to followed authors and authentication required |
| 7 | `007-comments` | F-CMT-1 to F-CMT-3 | articles, authentication | add, list, and delete the author's comment |
| 8 | `008-tags` | F-TAG-1 | articles | list available tags |

F-UI-1 to F-UI-3 are outside this API construction site. They are a frontend extension to handle after the API conformance suite is green, using the same cycle.

## 4.4 Feature 001, authentication and current user

Create the feature branch from `develop` before running Spec Kit:

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feat/001-user-authentication
```

Framing command:

```text
/speckit-specify
Implement F-AUTH-1 to F-AUTH-4 from docs/prd/PRD-conduit.md. Cover registration, login, GET /api/user, and PUT /api/user. Respect R-8 and R-9, the User format, JWT, and the Authorization: Token header. Out of scope: profiles, articles, comments, favorites, and tags.
```

During `clarify`, decide field validation, behavior for an existing email or username, JWT lifetime, and missing or invalid token cases. Evidence must include `422` errors, no password in any response, and persistence of a hashed password.

```bash
HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
```

Do not move to feature 002 until the scenarios in `specs/001-user-authentication/` and the HTTP evidence are green.

## 4.5 Feature 002, profiles and following

Create the feature branch from `develop` before running Spec Kit:

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feat/002-profiles-and-following
```

```text
/speckit-specify
Implement F-PROF-1 and F-PROF-2. Add GET /api/profiles/:username, POST /follow, and DELETE /follow. The following field is relative to the current user and is false for an anonymous visitor. Out of scope: articles, comments, and favorites.
```

Clarify following yourself, an unknown user, repeated follow, and the exact effect of unfollow. Verify that the public response never exposes a password and that anonymous responses remain consistent.

```bash
./mvnw test
HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
```

## 4.6 Feature 003, article CRUD

Create the feature branch from `develop` before running Spec Kit:

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feat/003-articles-crud
```

```text
/speckit-specify
Implement F-ART-3 to F-ART-6. Cover GET /articles/:slug, POST /articles, PUT, and DELETE. Generate the slug according to R-1. Only the author may edit or delete. Article lists must omit body according to R-7.
```

Clarify slug collisions, missing articles, authorization by another member, required creation fields, and optional update fields. Add HTTP tests before implementation tasks. Verify `401`, `403`, and `404` separately.

```bash
./mvnw test
HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
```

## 4.7 Feature 004, listing, filters, and pagination

Create the feature branch from `develop` before running Spec Kit:

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feat/004-articles-listing
```

```text
/speckit-specify
Implement F-ART-1 and R-2, R-3, R-7, and R-10. Cover GET /api/articles with tag, author, favorited, limit, and offset, and omit body from every list item.
```

Do not leave ambiguities implicit. Decide with `clarify`: filter combination, unknown filter values, `limit` bounds, negative offsets, and stable ordering for equal timestamps. Verify descending date order, default values, and consistency between pages.

```bash
./mvnw test
HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
```

This feature exposes vague specifications most clearly. Compare the gaps before and after clarification in `traceability.md`.

## 4.8 Feature 005, favorites

Create the feature branch from `develop` before running Spec Kit:

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feat/005-favorites
```

```text
/speckit-specify
Implement F-FAV-1. Cover POST and DELETE /api/articles/:slug/favorite. The favorited field and favoritesCount must remain consistent in article responses. Out of scope: feed and comments.
```

Clarify repeated favoriting, removing an absent favorite, missing articles, and counter visibility for an anonymous visitor. Verify the effect on the `favorited` filter from feature 004.

```bash
./mvnw test
HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
```

## 4.9 Feature 006, personal feed

Create the feature branch from `develop` before running Spec Kit:

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feat/006-personal-feed
```

```text
/speckit-specify
Implement F-ART-2 and R-4. Cover GET /api/articles/feed with limit and offset. The feed requires a valid JWT and contains only articles from followed authors.
```

Clarify no followed authors, an article deleted between reads, pagination values, and the relative `following` and `favorited` fields. Prove that an article by an unfollowed author does not appear.

```bash
./mvnw test
HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
```

## 4.10 Feature 007, comments

Create the feature branch from `develop` before running Spec Kit:

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feat/007-comments
```

```text
/speckit-specify
Implement F-CMT-1 to F-CMT-3. Cover POST /api/articles/:slug/comments, GET comments, and DELETE comments/:id. Only the comment author may delete it.
```

Clarify an empty comment, missing articles, missing comments, deletion by another member, and anonymous reading. Verify the Comment format and relative profile fields in every response.

```bash
./mvnw test
HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
```

## 4.11 Feature 008, tags

Create the feature branch from `develop` before running Spec Kit:

```bash
git switch develop
git pull --ff-only origin develop
git switch -c feat/008-tags
```

```text
/speckit-specify
Implement F-TAG-1 with GET /api/tags. The response exposes tags used by articles according to the PRD Tags format. Out of scope: free-text search and administration.
```

Clarify ordering, duplicates, casing, and the result when no article has a tag. Verify that the endpoint is accessible without authentication.

```bash
./mvnw test
HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
```

## 4.12 Final scope verification

After all eight features:

```bash
./mvnw verify
HOST=http://localhost:8080/api ./conformance/run-api-tests-hurl.sh
HOST=http://localhost:8080/api ./conformance/run-api-tests-bruno.sh
```

The construction site is complete when:

- every Must feature in the PRD is linked to a `specs/` directory;
- every endpoint in section 7 has `AC-*` scenarios and evidence;
- rules R-1 to R-10 are covered by traceability;
- Hurl is green for the complete API scope;
- Bruno has been verified as derived from Hurl;
- `spec.md`, `plan.md`, `test-cases.yaml`, `traceability.md`, and `tasks.md` exist for every feature.

## Sources

- [Conduit PRD](../prd/PRD-conduit.md)
- [Local RealWorld specifications](../prd/specifications/README.md)
- [Hurl conformance](../../conformance/hurl/README.md)
- [Architecture conventions](../../.github/instructions/hexagonal-architecture.instructions.md)
