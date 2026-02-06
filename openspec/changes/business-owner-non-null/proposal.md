## Why

To ensure data integrity and clear ownership, every business must have a designated owner. Currently, the owner field is nullable, which can lead to orphaned businesses without clear accountability.

## What Changes

- Add NOT NULL constraint to the owner_id column in the business table
- Update the Business entity to enforce non-null owner
- Ensure all business creation/update flows properly assign an owner
- Update related services and controllers to handle the constraint

## Capabilities

### New Capabilities
- `business-owner-required`: Enforce that every business has an owner
- `non-null-owner-constraint`: Apply database-level constraint for owner field

## Impact

- Database: New constraint on business.owner_id column
- Backend: Updated entity validation and business logic
- Frontend: Potentially updated forms to ensure owner is always provided

## Risks

- Existing orphaned businesses may violate the constraint
- Need to handle data migration carefully
- Potential impact on business creation flows