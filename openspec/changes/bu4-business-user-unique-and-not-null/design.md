## Context

BusinessUser models membership of a User in a Business. We must ensure each membership row references both a Business and a User (non-null), and that there are no duplicate memberships for the same (business, user) pair. This improves data integrity and supports predictable authorization and UI behavior.

## Goals / Non-Goals

Goals:
- Add NOT NULL constraints to business_id and user_id in business_user table.
- Add UNIQUE constraint on (business_id, user_id).
- Align domain annotations to express non-null relationships.

Non-Goals:
- Changing entity relationships beyond constraints.
- Backfilling or deleting invalid production data automatically.

## Decisions

1) Use Liquibase to add constraints
- addNotNullConstraint for business_id and user_id (columnDataType bigint)
- addUniqueConstraint for (business_id, user_id) with a deterministic name (e.g., uq_business_user_business_user)
- Pre-conditions: Warn/fail when nulls or duplicates exist; do not attempt automated cleanup.

2) Domain annotations to reflect constraints
- Annotate BusinessUser.business and BusinessUser.user with @NotNull
- Map join columns as nullable=false (via @JoinColumn) to match DB not-null enforcement
- Rationale: Fail fast at JPA validation and ensure schema generation aligns with Liquibase constraints

3) Compatibility and cross-DB support
- Use standard Liquibase tags addNotNullConstraint and addUniqueConstraint (supported on H2 and MySQL).

## Risks / Trade-offs

- Existing data may violate constraints → Mitigation: pre-conditions and migration checklist to fix data before applying.
- Application code paths that create BusinessUser must supply both associations; otherwise, persistence will fail → Mitigation: tests and DTO validations.

## Migration Plan

- Apply Liquibase changeset after verifying no nulls or duplicates in business_user.
- Update domain annotations and recompile.
- Rollback: drop the unique constraint and relax not-null if necessary (not automated here).

## Open Questions

- Constraint name conventions: adopt uq_business_user_business_user (or project-specific naming policy)?
- Should we add application-level validation to return 400 for null associations earlier (DTO validation)? Currently write APIs are disabled; leave as future consideration.
