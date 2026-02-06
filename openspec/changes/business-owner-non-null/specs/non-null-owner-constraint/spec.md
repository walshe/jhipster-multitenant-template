## ADDED Requirements

### Requirement: Non-Null Owner Database Constraint
The system SHALL enforce a NOT NULL constraint on the owner_id column in the business table.

#### Scenario: Database constraint enforcement
- **WHEN** a business record is stored in the database
- **THEN** the owner_id column must contain a valid value (cannot be NULL)

#### Scenario: Constraint violation
- **WHEN** an attempt is made to insert or update a business record with a NULL owner_id
- **THEN** the database shall reject the operation with a constraint violation error