## ADDED Requirements

### Requirement: Owner-Only Business Modification
The system SHALL restrict business modification operations to only the business owner.

#### Scenario: Non-owner attempts business modification
- **GIVEN** a user is authenticated and is a member of a business but not the owner
- **WHEN** the user attempts to update business information via PUT/PATCH/DELETE endpoints
- **THEN** the system returns HTTP 403 Forbidden

#### Scenario: Owner modifies business
- **GIVEN** a user is authenticated and is the owner of a business
- **WHEN** the user performs update operations on the business
- **THEN** the system allows the modification to proceed

#### Scenario: Non-member attempts business modification
- **GIVEN** a user is authenticated but not a member of a business
- **WHEN** the user attempts to modify business information
- **THEN** the system returns HTTP 404 Not Found (to avoid revealing business existence)

### Requirement: Owner-Only API Access
The system SHALL restrict access to business modification APIs to only business owners.

#### Scenario: Owner accesses modification API
- **GIVEN** a user is authenticated and is the owner of a business
- **WHEN** the user calls business modification APIs (update, delete, etc.)
- **THEN** the system grants access to the API endpoint

#### Scenario: Non-owner accesses modification API
- **GIVEN** a user is authenticated but is not the owner of a business
- **WHEN** the user attempts to call business modification APIs
- **THEN** the system denies access with appropriate error response