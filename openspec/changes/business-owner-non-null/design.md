## Context

Currently, the Business entity allows null values for the owner field, which can lead to orphaned businesses without clear ownership. We need to enforce that every business has a designated owner.

## Goals / Non-Goals

Goals:
- Make the owner field in the Business entity non-null
- Add NOT NULL constraint to the owner_id column in the database
- Ensure all existing businesses have a valid owner
- Update related code to handle the non-null constraint

Non-Goals:
- Changing the business ownership model
- Modifying user permissions beyond what's necessary for this constraint
- Refactoring unrelated business logic

## Decisions

1) Database constraint enforcement
- Add NOT NULL constraint to owner_id column in business table
- Use Liquibase to apply the constraint safely

2) Data integrity
- Ensure all existing businesses have a valid owner before applying the constraint
- Handle any orphaned businesses by assigning a default owner or cleaning them up

3) Entity validation
- Update Business entity to enforce non-null owner at the JPA level
- Add appropriate validation annotations

## Risks / Trade-offs

- Existing orphaned businesses may cause constraint violations
- Potential downtime during migration if not handled properly
- Need to ensure all business creation flows properly assign an owner

## Migration Plan

- Identify and handle any existing businesses without owners
- Apply database constraint via Liquibase changeset
- Update entity and related code
- Test thoroughly to ensure no regressions

## Open Questions

- How should we handle existing businesses that don't have an owner?
- Should we assign a system default owner or clean up orphaned records?