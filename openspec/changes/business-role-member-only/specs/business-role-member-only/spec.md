## ADDED Requirements

### Requirement: BusinessRole enum is restricted to MEMBER
The system SHALL define the BusinessRole enumeration with a single value MEMBER across backend and frontend codebases.

#### Scenario: Backend enum only includes MEMBER
- **WHEN** the backend builds and exposes API models that include BusinessRole
- **THEN** the only valid enumeration constant SHALL be MEMBER

#### Scenario: Frontend enum only includes MEMBER
- **WHEN** the frontend compiles and renders any UI that references BusinessRole
- **THEN** the only valid enumeration option SHALL be MEMBER

### Requirement: API accepts only MEMBER for BusinessUser.role
The system SHALL validate BusinessUser.role on create/update requests and accept only MEMBER. Any other value MUST be rejected with a 400 error and a clear validation message.

#### Scenario: Create BusinessUser with invalid role
- **WHEN** a client submits POST /api/business-users with role = OWNER or ADMIN (or any non-MEMBER value)
- **THEN** the API SHALL respond 400 Bad Request and indicate that role must be MEMBER

#### Scenario: Update BusinessUser with invalid role
- **WHEN** a client submits PUT/PATCH /api/business-users/{id} with role = OWNER or ADMIN (or any non-MEMBER value)
- **THEN** the API SHALL respond 400 Bad Request and indicate that role must be MEMBER

### Requirement: Persisted data contains only MEMBER values
The data store for business_user.role SHALL contain only the value MEMBER after migration and for all subsequent writes.

#### Scenario: Data normalization on deployment
- **WHEN** the system is deployed with this change
- **THEN** all existing rows in business_user with role <> 'MEMBER' SHALL be updated to 'MEMBER'

#### Scenario: Prevent future invalid values
- **WHEN** any insert or update attempts to persist a non-MEMBER role value to business_user.role
- **THEN** the operation SHALL be rejected by a database constraint or application validation

### Requirement: UI only displays and defaults to MEMBER
The BusinessUser role control in the UI SHALL present only MEMBER and default to MEMBER for new records.

#### Scenario: Create form default
- **WHEN** the user opens the BusinessUser creation form
- **THEN** the role field SHALL be pre-populated with MEMBER and SHALL offer no other selectable options

#### Scenario: Edit form display
- **WHEN** the user edits an existing BusinessUser
- **THEN** the role field SHALL display MEMBER and SHALL offer no other selectable options

### Requirement: OpenAPI schema enumerates only MEMBER
The generated OpenAPI documentation for any schema containing BusinessUser.role SHALL enumerate only MEMBER.

#### Scenario: OpenAPI generation
- **WHEN** the OpenAPI JSON/YAML is generated
- **THEN** the enum for the role property in BusinessUser (and related DTOs) SHALL list only MEMBER

### Requirement: Authorization shall not rely on BusinessUser.role for elevated permissions
Authorization logic SHALL use Business.owner for ownership checks and BusinessUser membership existence for membership checks. No elevated permissions SHALL be inferred from BusinessUser.role values.

#### Scenario: Owner check
- **WHEN** the system verifies whether a user is the owner of a business
- **THEN** it SHALL determine ownership from Business.owner and NOT from BusinessUser.role

#### Scenario: Membership check
- **WHEN** the system verifies whether a user is a member of a business
- **THEN** it SHALL determine membership from the existence of a BusinessUser record linking the user and business (or via ownership implying membership), and NOT from role values other than MEMBER

### Requirement: Internationalization strings only include MEMBER
The i18n resources for BusinessRole SHALL include only the MEMBER key; removed keys SHALL not render in the UI.

#### Scenario: i18n rendering
- **WHEN** any UI component renders a translated BusinessRole value
- **THEN** the translation source SHALL contain only MEMBER and SHALL not include OWNER or ADMIN