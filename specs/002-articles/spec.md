# Feature Specification: Article Publishing and Discovery

**Feature Branch**: `002-articles`

**Created**: 2026-09-06

**Status**: Draft

**Input**: User description: "Iteration 2 of Conduit: implement article publishing and discovery after authentication. Users must be able to create, read, update, and delete articles, attach tags, browse public article lists, filter by author or tag, paginate results, and receive the RealWorld article response contract."

## Clarifications

### Session 2026-09-06

- Q: Que doit faire l’API lorsqu’un lecteur fournit une pagination invalide ou négative, par exemple `limit=-1` ou `offset=-5` ? → A: Retourner `422` avec une erreur sur `limit` ou `offset`.
- Q: Lorsqu’un auteur modifie le titre d’un article, le slug doit-il rester identique ou être régénéré ? → A: Régénérer le slug à partir du nouveau titre, en garantissant son unicité.
- Q: Comment le système doit-il générer un slug lorsqu’un nouvel article utilise un titre déjà présent ? → A: Accepter le doublon et générer un slug unique pour chaque article, conformément au contrat RealWorld.
- Q: Les tags doivent-ils être sensibles à la casse ou normalisés en minuscules ? → A: Normaliser tous les tags en minuscules et supprimer les doublons après normalisation.

## User Scenarios & Testing

### User Story 1 - Publish and manage an article (Priority: P1)

As an authenticated author, I want to create, update, and delete my own articles so
that I can publish and maintain content in Conduit.

**Why this priority**: Article ownership is the foundation for every later social
interaction and feed feature.

**Independent Test**: Authenticate an existing user, create an article with tags,
retrieve it, update its body and tags, verify persistence, then delete it and verify
that it is no longer available.

**Acceptance Scenarios**:

1. **Given** an authenticated user and valid title, description, body, and optional
   tags, **When** the user creates an article, **Then** the system returns `201` with
   a complete article including a unique slug, timestamps, tags, author, and zero
   favorites.
2. **Given** an existing article owned by the authenticated user, **When** the user
  updates accepted fields, **Then** the system returns `200`, preserves omitted
  fields, updates the timestamp, persists the changes, and regenerates a unique
  slug when the title changes.
3. **Given** an existing article owned by the authenticated user, **When** the user
   deletes it, **Then** the system returns `204` and subsequent retrieval returns
   `404`.
4. **Given** an article update without `tagList`, **When** the update is processed,
   **Then** existing tags are preserved; an empty list removes all tags; a null list
   is rejected with `422`.

### User Story 2 - Browse and discover public articles (Priority: P1)

As an anonymous or authenticated reader, I want to retrieve articles and filter the
public list so that I can discover content without needing to create an account.

**Why this priority**: Public discovery is the primary reader experience and must
work independently of the social features planned for the next iteration.

**Independent Test**: Create tagged articles, retrieve one article anonymously, browse
all articles anonymously, filter by author and tag, and verify that list responses
contain metadata but omit article bodies.

**Acceptance Scenarios**:

1. **Given** a published article, **When** an anonymous reader requests it by slug,
   **Then** the system returns `200` with its complete body, tags, timestamps,
   author, `favorited: false`, and `favoritesCount: 0`.
2. **Given** published articles, **When** an anonymous reader requests the public
   list, **Then** the system returns `200` with `articles` and `articlesCount`, and
   each list item omits `body`.
3. **Given** an author or tag filter, **When** a reader requests the public list,
   **Then** only matching articles are returned and the count reflects the complete
   filtered result set.
4. **Given** an authenticated reader, **When** the reader requests public articles,
  **Then** the response remains contract-compatible and includes the favorite fields
  with the anonymous/default `favorited: false` state; reader-specific favorite
  management is deferred to iteration 3.

### User Story 3 - Page through article results (Priority: P2)

As a reader, I want bounded pages and offsets so that I can browse large article
collections predictably.

**Why this priority**: Pagination makes public discovery usable as the collection
expands and establishes the contract needed by the future feed.

**Independent Test**: Create two or more articles for one author, request a page with
`limit=1`, request the next page with `offset=1`, and verify order, page size, and
total count.

**Acceptance Scenarios**:

1. **Given** two articles created in sequence, **When** the reader requests
   `limit=1`, **Then** the newest article is returned and `articlesCount` remains the
   total matching count.
2. **Given** the same collection, **When** the reader requests `limit=1&offset=1`,
   **Then** the next article is returned without changing the total count.
3. **Given** invalid or negative pagination values, **When** the list is requested,
   **Then** the system returns `422` with a field-specific error on `limit` or
   `offset`.

### User Story 4 - Read the tag catalogue (Priority: P2)

As a reader, I want to retrieve the tags currently used by published articles so
that I can navigate discovery categories.

**Why this priority**: Tags are part of article publication and support the public
tag filter without introducing social graph behavior.

**Independent Test**: Create an article with two tags, request the tag catalogue, and
verify both tags are present.

**Acceptance Scenarios**:

1. **Given** published articles with tags, **When** a reader requests the tag
   catalogue, **Then** the system returns `200` with a list containing each used tag.
2. **Given** no matching article for a requested tag, **When** the reader filters the
   article list, **Then** the system returns an empty list and count `0`.

### Edge Cases

- Creating, updating, or deleting an article without a valid `Token` authorization
  returns `401` with the standard error envelope.
- Updating or deleting an article owned by another user returns `403` with an
  ownership error and leaves the article unchanged.
- Reading an unknown slug returns `404` with an article error.
- Creating an article with an empty title, description, or body returns `422` with
  a field-specific error.
- Duplicate titles are allowed, but generated slugs remain unique.
- Tags are normalized to lowercase and deduplicated after normalization for
  storage, filtering, and catalogue responses.
- A list response never includes `body`, while a single-article response does.
- Anonymous article responses expose `favorited: false` and `favoritesCount: 0`.
- Article authors include the public username, bio, image, and following state.

## Requirements

### Functional Requirements

- **FR-001**: Authenticated users MUST be able to create an article with a non-empty
  title, description, body, and optional tag list.
- **FR-002**: Each created article MUST have a unique slug, creation timestamp,
  update timestamp, author projection, tag list, favorite state, and favorite count.
- **FR-002a**: When an article title changes, the system MUST regenerate a unique
  slug from the new title; the previous slug MUST no longer be the canonical slug.
- **FR-003**: Authenticated article owners MUST be able to update title, description,
  body, and tag list while omitted fields retain their current values.
- **FR-004**: Authenticated article owners MUST be able to delete their own articles,
  while non-owners MUST receive `403` and the article MUST remain unchanged.
- **FR-005**: Readers MUST be able to retrieve a single article by slug anonymously
  or while authenticated.
- **FR-006**: Readers MUST be able to retrieve a public article list anonymously or
  while authenticated.
- **FR-007**: Public article lists MUST support filtering by author and tag.
- **FR-008**: Public article lists MUST support `limit` and `offset`, return newest
  articles first, and report the total matching count separately from page size.
- **FR-008a**: Invalid or negative `limit` and `offset` values MUST return `422`
  with a field-specific error.
- **FR-009**: List article representations MUST omit `body`; single article
  representations MUST include `body`.
- **FR-010**: The system MUST expose the catalogue of tags used by published
  articles.
- **FR-010a**: Tags MUST be normalized to lowercase and deduplicated after
  normalization before persistence, filtering, or catalogue responses.
- **FR-011**: Missing, malformed, or invalid authentication on protected article
  operations MUST return `401` using the established error envelope.
- **FR-012**: Invalid article input MUST return `422` with field-keyed validation
  errors; unknown articles MUST return `404`.
- **FR-013**: Article endpoints MUST preserve the RealWorld response envelopes,
  content type, `Token` authentication scheme, author projection, and anonymous
  favorite defaults defined by the PRD and conformance artifacts.
- **FR-014**: Article persistence MUST remain compatible with the existing user
  accounts and authentication behavior from iteration 1.

### Key Entities

- **Article**: A published content item with title, description, body, unique slug,
  timestamps, tags, author, favorite count, and viewer-specific favorite state.
- **Tag**: A normalized discovery label associated with one or more articles.
- **ArticleAuthor**: The public author projection used in article responses, derived
  from an existing user account.
- **ArticleQuery**: The reader's optional author/tag filters and pagination values.

## Success Criteria

### Measurable Outcomes

- **SC-001**: An authenticated user can create, retrieve, update, and delete an
  article through the HTTP boundary in one end-to-end scenario.
- **SC-002**: All article-scoped article, pagination, tag, authentication, validation,
  and ownership Hurl scenarios pass against a PostgreSQL-backed runtime before the
  iteration is declared complete. Feed, favorite, and comment scenarios remain
  deferred to iteration 3 and are not gates for this feature.
- **SC-003**: At least 100% of article endpoints have an HTTP integration test covering
  success, authentication, validation, serialization, and ownership behavior.
- **SC-004**: Public list responses contain no article body field, while single-article
  responses contain the exact requested body in every tested scenario.
- **SC-005**: A filtered and paginated article request returns the correct page size
  and total matching count for collections containing at least 100 articles.
- **SC-006**: A reader can discover a newly used tag and retrieve all matching articles
  without authentication.

## Assumptions

- Iteration 1 authentication and current-user management are available and remain
  contract-compatible.
- Article creation is restricted to authenticated users; public reading and tag
  discovery are anonymous.
- Article favorites, profile following, comments, and the personalized feed are
  deferred to iteration 3; this iteration exposes their default article fields only.
- PostgreSQL and Flyway remain the persistence and schema sources of truth.
- Hurl scenarios and the RealWorld PRD are authoritative when an implementation
  preference conflicts with an external response contract.
- This iteration does not include an editor UI; the API behavior is the deliverable.

## Out of Scope

- Favoriting and unfavoriting articles.
- Profiles and following relationships.
- Comments and comment deletion.
- Personalized feeds.
- Frontend screens or client-side state management.
