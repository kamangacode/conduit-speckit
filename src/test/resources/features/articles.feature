Feature: Article publishing and discovery

  @AC-US1-001 @FR-001 @FR-002 @FR-010a @FR-013 @FR-014
  Scenario: Create an article with normalized tags
    Given an authenticated author and valid article fields with mixed-case duplicate tags
    When the author creates the article
    Then the article response has status 201, a unique slug, timestamps, public author, normalized tags, favorited false, and favoritesCount 0

  @AC-US1-002 @FR-002a @FR-003 @FR-010a @FR-013
  Scenario: Update an owned article
    Given an authenticated author owns an article
    When the author updates its title, body, and tags
    Then the article response has status 200, preserves omitted fields, persists changes, and has a regenerated unique slug

  @AC-US1-003 @FR-004 @FR-012 @FR-013
  Scenario: Delete an owned article
    Given an authenticated author owns an article
    When the author deletes it
    Then deletion has status 204 and later retrieval has status 404 with an article error

  @AC-US1-004 @FR-003 @FR-010a @FR-012
  Scenario: Preserve, clear, or reject article tags on update
    Given an authenticated author owns an article with tags
    When tagList is omitted, empty, or null in an update
    Then omitted tags are preserved, empty tags are removed, and null tagList has status 422

  @AC-US2-001 @FR-005 @FR-009 @FR-013
  Scenario: Retrieve a published article anonymously
    Given a published article
    When an anonymous reader retrieves it by slug
    Then the article response has status 200 and includes its body and anonymous favorite defaults

  @AC-US2-002 @FR-006 @FR-009 @FR-013
  Scenario: List published articles anonymously
    Given published articles
    When an anonymous reader lists articles
    Then the list response has status 200, articlesCount, and no article body

  @AC-US2-003 @FR-006 @FR-007 @FR-009 @FR-013
  Scenario: Filter public articles by author and tag
    Given published articles with distinct authors and tags
    When a reader filters articles by author or tag
    Then only matching body-free articles are returned and articlesCount is the complete filtered count

  @AC-US2-004 @FR-006 @FR-013 @FR-014
  Scenario: List articles as an authenticated reader
    Given an authenticated reader and published articles
    When the reader lists public articles
    Then the list response has status 200 and includes the reader-specific favorite state

  @AC-US3-001 @FR-008 @FR-009 @FR-013
  Scenario: Retrieve the newest first page
    Given two articles created in sequence
    When a reader requests limit 1
    Then the newest body-free article is returned and articlesCount remains the total

  @AC-US3-002 @FR-008 @FR-009 @FR-013
  Scenario: Retrieve an offset page
    Given two articles created in sequence
    When a reader requests limit 1 and offset 1
    Then the next body-free article is returned and articlesCount remains unchanged

  @AC-US3-003 @FR-008a @FR-012
  Scenario: Reject invalid pagination
    Given an invalid or negative limit or offset
    When a reader lists articles
    Then the response has status 422 with an error for limit or offset

  @AC-US4-001 @FR-010 @FR-010a @FR-013
  Scenario: List the tag catalogue
    Given published articles with normalized tags
    When a reader requests the tag catalogue
    Then the response has status 200 and every used tag appears once

  @AC-US4-002 @FR-007 @FR-010 @FR-013
  Scenario: Filter by an unused tag
    Given no published article uses a requested tag
    When a reader filters the article list
    Then the response has status 200 with an empty article list and articlesCount 0

  @FR-011 @FR-012 @FR-013
  Scenario: Reject unauthenticated or unauthorized article mutations
    Given an article owned by another authenticated author
    When a visitor mutates it without a valid Token or a different author mutates it
    Then the response has status 401 or 403 and the article remains unchanged