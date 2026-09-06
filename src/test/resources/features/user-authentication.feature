Feature: User authentication

  @AC-US1-001 @AC-US1-004 @FR-001 @FR-003 @FR-004 @FR-013 @FR-014
  Scenario: Register a valid account
    Given no member uses the email "cucumber@example.invalid"
    When the visitor registers with:
      | username | cucumber-user            |
      | email    | cucumber@example.invalid |
      | password | password123              |
    Then the response status is 201
    And the response contains a user with username "cucumber-user"
    And the response does not contain a password

  @AC-US3-002 @FR-012
  Scenario: Reject an unauthenticated current-user request
    When the visitor requests the current user without a token
    Then the response status is 401
    And the response does not contain account data