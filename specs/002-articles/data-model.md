# Data Model: Article Publishing and Discovery

## Article

| Field | Rules |
|---|---|
| id | Stable internal identifier |
| title | Required, non-blank; duplicates are allowed |
| slug | Required and unique; regenerated from title changes |
| description | Required, non-blank |
| body | Required, non-blank; omitted from list projections |
| createdAt | Set when the article is created |
| updatedAt | Set on each accepted update |
| authorId | References an existing User account |
| tags | Zero or more normalized Tag values |

Lifecycle: create -> update -> delete. Only the author may update or delete. An update omitting a
field preserves it; omitting `tagList` preserves tags, an empty list removes all tags, and null
`tagList` is invalid.

## Tag

| Field | Rules |
|---|---|
| value | Lowercase normalized value, unique for an Article |

Tags are normalized and deduplicated before persistence, filtering, and catalogue projection.

## ArticleQuery

| Field | Rules |
|---|---|
| author | Optional public username filter |
| tag | Optional normalized tag filter |
| limit | Optional non-negative bounded page size |
| offset | Optional non-negative page offset |

Results are newest first. `articlesCount` is the total count after filters and before pagination.

## Article Projection

Single-article responses contain all Article fields, public author profile, `favorited`, and
`favoritesCount`. List responses use the same projection except `body` is absent. This feature
returns the anonymous favorite defaults and does not add favorite management.