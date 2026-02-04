## ADDED Requirements

### Requirement: Owner-only visibility for BusinessUser list
The system SHALL return BusinessUser rows only for Businesses owned by the authenticated user when invoking GET /api/business-users.

#### Scenario: Owner lists members
- **WHEN** the owner of Business X requests GET /api/business-users
- **THEN** the response contains only BusinessUser rows whose business is X (and any other businesses owned by the user)

#### Scenario: Non-owner lists members
- **WHEN** a user who does not own any Businesses requests GET /api/business-users
- **THEN** the response list is empty and the count is 0

### Requirement: Owner-only visibility for BusinessUser view by id
The system MUST return 404 (Not Found) when an authenticated user requests GET /api/business-users/{id} for a BusinessUser whose associated Business is not owned by the user.
The system MUST return 200 (OK) when the authenticated user owns the associated Business.

#### Scenario: Non-owner fetches BusinessUser by id
- **WHEN** a non-owner requests GET /api/business-users/{id} for a BusinessUser whose business is owned by another user
- **THEN** the response status is 404 (Not Found)

#### Scenario: Owner fetches BusinessUser by id
- **WHEN** the owner of Business Y requests GET /api/business-users/{id} for a BusinessUser of Business Y
- **THEN** the response status is 200 (OK) and the body contains the BusinessUser

### Requirement: Count reflects owner-only visibility
The system SHALL return GET /api/business-users/count equal to the number of BusinessUser rows associated to Businesses owned by the caller.

#### Scenario: Owner counts members
- **WHEN** the owner of 2 Businesses with total 5 members requests GET /api/business-users/count
- **THEN** the count equals 5
