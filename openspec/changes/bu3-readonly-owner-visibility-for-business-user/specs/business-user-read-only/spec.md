## ADDED Requirements

### Requirement: BusinessUser API is read-only
The system SHALL expose only list and view operations for BusinessUser via the API.
The system MUST disable create, update, patch, and delete operations for BusinessUser.

#### Scenario: List allowed
- **WHEN** a client invokes GET /api/business-users
- **THEN** the system returns a paginated list subject to visibility constraints

#### Scenario: View by id allowed
- **WHEN** a client invokes GET /api/business-users/{id}
- **THEN** the system returns 200 with the entity if visible, or 404 if not visible

#### Scenario: Create disabled
- **WHEN** a client invokes POST /api/business-users
- **THEN** the system responds with 405 (Method Not Allowed) or no mapping is present

#### Scenario: Update disabled
- **WHEN** a client invokes PUT /api/business-users/{id}
- **THEN** the system responds with 405 (Method Not Allowed) or no mapping is present

#### Scenario: Patch disabled
- **WHEN** a client invokes PATCH /api/business-users/{id}
- **THEN** the system responds with 405 (Method Not Allowed) or no mapping is present

#### Scenario: Delete disabled
- **WHEN** a client invokes DELETE /api/business-users/{id}
- **THEN** the system responds with 405 (Method Not Allowed) or no mapping is present
