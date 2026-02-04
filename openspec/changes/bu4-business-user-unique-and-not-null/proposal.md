## Why

To ensure data integrity for memberships, each (business, user) pair in BusinessUser must be unique and both relationships must be present. Without these constraints, duplicate memberships or null relationships could appear, leading to inconsistent authorization and UI behavior.

## What Changes

- Database constraints for business_user:
  - Add NOT NULL constraints to business_id and user_id columns.
  - Add a UNIQUE constraint on the pair (business_id, user_id).
- Domain annotations:
  - Mark BusinessUser.business and BusinessUser.user as @NotNull and map join columns as nullable = false.
- Migration behavior:
  - Liquibase change will fail if duplicates or nulls exist; these must be cleaned prior to applying in shared environments.

## Capabilities

### New Capabilities
- `business-user-unique-membership`: Enforce uniqueness of (business, user) in BusinessUser.
- `business-user-non-null-relationships`: Enforce non-null business and user relationships in BusinessUser.

## Impact

- Database: new constraints on business_user table.
- Backend: stricter validation at entity level; save operations (if used in other internal flows) will fail fast on nulls.
- Tests: Adjust or add tests to reflect constraint enforcement at persistence layer.

## Risks

- Existing data may violate constraints (nulls or duplicates). Migration will fail until data is cleaned.
