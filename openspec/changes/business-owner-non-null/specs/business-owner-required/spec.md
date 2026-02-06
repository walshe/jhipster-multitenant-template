## ADDED Requirements

### Requirement: Business Owner Required
The system SHALL enforce that every business has a designated owner.

#### Scenario: Create business with owner
- **WHEN** a new business is created
- **THEN** the system requires an owner to be specified

#### Scenario: Update business owner
- **WHEN** a business is updated
- **THEN** the system ensures the owner field remains non-null

#### Scenario: Null owner rejection
- **WHEN** an attempt is made to create or update a business with a null owner
- **THEN** the system rejects the operation with an appropriate error