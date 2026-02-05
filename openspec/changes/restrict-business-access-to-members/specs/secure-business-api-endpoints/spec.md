## ADDED Requirements

### Requirement: Secure Business Retrieval API
The system SHALL secure business retrieval endpoints to only return businesses the user is a member of.

#### Scenario: User retrieves business list
- **GIVEN** a user is authenticated
- **WHEN** the user requests the list of businesses via GET /api/businesses
- **THEN** the system returns only businesses where the user is a member

#### Scenario: User retrieves specific business
- **GIVEN** a user is authenticated
- **WHEN** the user requests a specific business via GET /api/businesses/{id}
- **THEN** the system returns the business only if the user is a member, otherwise returns HTTP 404 Not Found

### Requirement: Secure Business Modification API
The system SHALL secure business modification endpoints to only allow modifications by business owners.

#### Scenario: Owner modifies business
- **GIVEN** a user is authenticated and is the owner of a business
- **WHEN** the user sends PUT/PATCH/DELETE request to business endpoints
- **THEN** the system processes the request and returns appropriate success response

#### Scenario: Non-owner attempts modification
- **GIVEN** a user is authenticated but is not the owner of a business
- **WHEN** the user sends PUT/PATCH/DELETE request to business endpoints
- **THEN** the system returns HTTP 403 Forbidden

### Requirement: Secure Business Creation API
The system SHALL validate business creation requests to ensure proper ownership assignment.

#### Scenario: User creates business
- **GIVEN** a user is authenticated
- **WHEN** the user creates a new business via POST /api/businesses
- **THEN** the system assigns the user as the owner of the newly created business