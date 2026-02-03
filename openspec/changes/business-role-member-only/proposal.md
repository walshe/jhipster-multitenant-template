## Why

To simplify authorization while multitenancy rules mature, we will temporarily collapse BusinessRole to a single value (MEMBER). OWNER/ADMIN role distinctions are not consistently enforced today and create confusion and dead code. Aligning the domain model with actual behavior reduces complexity and risk of unintended privilege checks.

## What Changes

- BREAKING: Collapse BusinessRole enum to only MEMBER; remove OWNER and ADMIN values from backend and frontend.
- Domain model: Update enumeration in code and JDL so BusinessUser.role only accepts MEMBER.
- Data migration: Normalize existing business_user.role values to MEMBER; tighten column validation to accept MEMBER only.
- Backend:
  - Replace any role-based branching with membership checks (or owner checks where explicitly required by existing logic).
  - Adjust DTO validation and REST contracts to only allow MEMBER.
  - Update tests and fixtures accordingly.
- Frontend:
  - Update enum model and any selection controls to only show MEMBER.
  - Remove translations for removed roles.
- Docs/OpenAPI: Ensure API schemas reflect a single enum value.

## Capabilities

### New Capabilities
- `business-role-member-only`: Collapse BusinessRole to MEMBER and align usages across backend, database, and UI so all business users are treated as members for authorization, with owner-specific checks continuing to rely on Business.owner.

### Modified Capabilities
- (none)

## Impact

- Code:
  - Backend enum: src/main/java/com/walshe/multitenant/domain/enumeration/BusinessRole.java
  - Domain usage: src/main/java/com/walshe/multitenant/domain/BusinessUser.java and related services/mappers/DTOs (e.g., BusinessUserDTO)
  - Authorization: Ensure services such as BusinessAuthorizationService/Util do not rely on ADMIN/OWNER roles; continue to use owner linkage and membership lookups.
- Frontend:
  - Enum model: src/main/webapp/app/shared/model/enumerations/business-role.model.ts
  - Forms/components referencing BusinessUser.role, and i18n: src/main/webapp/i18n/en/businessRole.json
- Data:
  - Liquibase migration to normalize existing values and restrict future values.
- Tests and fixtures updated to remove references to OWNER/ADMIN.
