## ADDED Requirements

### Requirement: Unique (business, user) membership
The system SHALL enforce that each (business, user) pair appears at most once in BusinessUser.

#### Scenario: Duplicate membership prevented
- **WHEN** a duplicate BusinessUser row is inserted for the same business_id and user_id
- **THEN** the database rejects it due to a UNIQUE constraint
