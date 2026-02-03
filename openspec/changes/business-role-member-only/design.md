## Context

The application is a JHipster-based, multitenant system. Authorization logic today primarily relies on:
- Business.owner linkage for owner checks
- Membership via BusinessUser records for membership checks

The BusinessRole enum currently defines OWNER, ADMIN, MEMBER, but role distinctions are not consistently enforced. Many code paths use owner and/or membership checks rather than role-based branching. This mismatch increases complexity and risk.

This change collapses BusinessRole to a single value (MEMBER) and aligns backend, database, and frontend. Owner capabilities continue to be modeled by Business.owner (not BusinessUser.role).

Key touchpoints:
- Backend enum: src/main/java/com/walshe/multitenant/domain/enumeration/BusinessRole.java
- Domain/DTOs: BusinessUser.role (e.g., BusinessUser, BusinessUserDTO)
- Authorization: BusinessAuthorizationService/Util use owner + membership (no ADMIN/OWNER role dependency)
- Frontend enum + forms: src/main/webapp/app/shared/model/enumerations/business-role.model.ts and BusinessUser forms
- i18n: src/main/webapp/i18n/en/businessRole.json
- Database: business_user.role values and constraints (Liquibase)

Constraints:
- Preserve existing owner semantics (Business.owner) and membership logic
- Avoid introducing a new permission model
- Keep changes reversible if future roles are reintroduced

## Goals / Non-Goals

Goals:
- Collapse BusinessRole to only MEMBER and ensure type-safety across backend and frontend
- Normalize existing data to MEMBER and prevent invalid values going forward
- Keep owner-specific behavior based on Business.owner (unchanged)
- Remove ADMIN/OWNER from UI and translations

Non-Goals:
- Introducing new roles/permissions or reworking authorization beyond this simplification
- Changing Business.owner behavior
- Broad refactors unrelated to role removal

## Decisions

1) Domain enum simplification
- Decision: Change BusinessRole.java to a single value MEMBER.
- Rationale: Matches current effective behavior; reduces dead branches and confusion.
- Alternative: Keep enum values but deprecate/non-display in UI only. Rejected to avoid lingering invalid states and backend/API ambiguity.

2) DTO/validation and REST contract
- Decision: BusinessUserDTO.role remains required but only MEMBER is allowed; default MEMBER on creation paths.
- Rationale: Keeps schema simple and explicit; ensures OpenAPI reflects the only valid value.
- Alternative: Make role nullable and infer MEMBER. Rejected to avoid null-handling and drift.

3) Database migration and constraint
- Decision: Liquibase changeset to normalize existing business_user.role values to 'MEMBER'. Add a CHECK constraint to restrict to 'MEMBER' (MySQL 8+ supports CHECK; otherwise rely on app validation if unavailable in target env).
- Rationale: Data integrity at source; prevents regressions.
- Alternative: Application-only validation. Accepted as fallback if CHECK not supported.

Example Liquibase YAML (sketch):
- Update invalid values
  - update business_user set role = 'MEMBER' where role in ('OWNER','ADMIN') or role is null or role <> 'MEMBER'
- Add check constraint (if supported)
  - addCheckConstraint tableName: business_user, constraintName: ck_business_user_role_member_only, checkConstraint: role = 'MEMBER'

4) Frontend enum and forms
- Decision: Update TS enum to only MEMBER; default form value to MEMBER and remove any selector options other than MEMBER.
- Rationale: Prevents user selection of removed roles, keeps UI consistent.
- Alternative: Keep enum values but hide in UI. Rejected for the same reasons as backend alternative.

5) Authorization behavior
- Decision: Do not introduce role-based branching. Owner checks continue via Business.owner, membership via BusinessUser. No ADMIN path.
- Rationale: Reflects real, enforced behavior today.

6) Internationalization
- Decision: Remove OWNER and ADMIN keys from businessRole.json, retain MEMBER.
- Rationale: Avoid dead translations and UI confusion.

7) Testing and compatibility
- Decision: Update backend and frontend tests/datasets to only use MEMBER. Verify OpenAPI enum for BusinessUser.role contains only MEMBER.
- Rationale: Keep CI green and contract accurate.

## Risks / Trade-offs
- Existing data with non-MEMBER roles → Mitigated by migration setting all to MEMBER.
- External clients depending on OWNER/ADMIN values → Breaking change; communicate in release notes and version API if necessary.
- Database CHECK constraint incompatibility across environments → Fallback to application validation only.
- Hidden code paths expecting ADMIN/OWNER → Searches and tests to validate; remove dead branches.
- Rollback complexity if future roles are reintroduced → Acceptable; reintroducing values is a backward-compatible expansion after constraint adjustment.

## Migration Plan
1) Create Liquibase changeset:
   - Normalize business_user.role to 'MEMBER'.
   - Attempt to add CHECK constraint role = 'MEMBER' (conditional by DB version if necessary).
2) Backend changes:
   - Update BusinessRole.java to single MEMBER.
   - Ensure BusinessUserDTO and any validation accept only MEMBER; default to MEMBER where new records are created.
   - Remove any references to OWNER/ADMIN in services/controllers (search and clean-up).
3) Frontend changes:
   - Update TS enum to only MEMBER and adjust forms to default to MEMBER; remove extra options.
   - Remove i18n keys for OWNER/ADMIN.
4) Regenerate OpenAPI docs and verify enum.
5) Testing:
   - Update and run unit/integration/e2e tests.
6) Deployment order:
   - Apply DB migration.
   - Deploy backend, then frontend.
7) Rollback:
   - Revert code changes, drop CHECK constraint, restore previous enum values; data remains MEMBER which is compatible.

## Open Questions
- Any external integrations or clients that rely on OWNER/ADMIN values? If yes, coordinate deprecation timeline.
- Should we keep a DB-level CHECK constraint across all environments, or rely solely on app-level validation for maximum compatibility?
- Are there any API endpoints that expose alternative role values through query params/filters that also need cleanup?
