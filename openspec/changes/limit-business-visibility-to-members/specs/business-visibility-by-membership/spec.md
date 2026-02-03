## ADDED Requirements

### Requirement: Limit Business list visibility to memberships
The system SHALL return only Businesses for which the authenticated user is a member or owner when invoking GET /api/businesses.
Pagination metadata (totalItems) SHALL reflect only Businesses visible to the user.

#### Scenario: Member lists their Businesses
- **WHEN** an authenticated user who is a member of Businesses A and B requests GET /api/businesses
- **THEN** the response contains only Businesses A and B and the total count equals 2

#### Scenario: Non-member lists Businesses
- **WHEN** an authenticated user who is not a member of any Business requests GET /api/businesses
- **THEN** the response list is empty and the total count equals 0

#### Scenario: Criteria filters with membership
- **WHEN** an authenticated user requests GET /api/businesses with filters (e.g., name.contains=Shop)
- **THEN** returned Businesses satisfy BOTH the membership constraint and the provided filters

### Requirement: Limit Business count to memberships
The system SHALL return a count equal to the number of Businesses visible to the authenticated user (member or owner) when invoking GET /api/businesses/count.

#### Scenario: Member counts Businesses
- **WHEN** an authenticated member/owner of 3 distinct Businesses requests GET /api/businesses/count
- **THEN** the count equals 3

#### Scenario: Non-member counts Businesses
- **WHEN** an authenticated user who is not a member of any Business requests GET /api/businesses/count
- **THEN** the count equals 0

### Requirement: Protect single Business retrieval by membership
The system MUST return 404 (Not Found) when an authenticated user requests GET /api/businesses/{id} for a Business they are not a member or owner of.
The system MUST return 200 (OK) with the Business body when the user is a member or owner of the requested Business.

#### Scenario: Non-member fetches Business by id
- **WHEN** an authenticated user requests GET /api/businesses/{id} for a Business they do not belong to
- **THEN** the response status is 404 (Not Found)

#### Scenario: Member fetches Business by id
- **WHEN** an authenticated user who is a member of Business A requests GET /api/businesses/{id} with A's id
- **THEN** the response status is 200 (OK) and the body contains Business A

#### Scenario: Owner fetches Business by id
- **WHEN** the owner of Business B requests GET /api/businesses/{id} with B's id
- **THEN** the response status is 200 (OK) and the body contains Business B

### Requirement: Ownership implies membership
The system SHALL treat Business ownership as sufficient to satisfy membership checks for visibility and single-entity retrieval.

#### Scenario: Owner included in list visibility
- **WHEN** the owner of Business C requests GET /api/businesses
- **THEN** Business C is included in the response even if the owner has no explicit BusinessUser row for C

### Requirement: Invitations do not confer visibility
The system MUST NOT treat pending or declined invitations as membership for visibility or single-entity retrieval purposes.

#### Scenario: Invited user cannot view Business
- **WHEN** a user with only a pending invitation to Business D requests GET /api/businesses/{id} with D's id
- **THEN** the response status is 404 (Not Found)

### Requirement: No information leakage
For non-members, the system MUST NOT reveal the existence of a Business through response content or error details.

#### Scenario: Non-member receives indistinguishable 404
- **WHEN** a non-member requests GET /api/businesses/{id} for any id (existing or not)
- **THEN** the response status is 404 (Not Found) with no details that indicate whether the Business exists
