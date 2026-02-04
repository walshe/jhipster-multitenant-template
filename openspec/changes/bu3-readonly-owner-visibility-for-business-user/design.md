## Context

BusinessUser currently exposes full CRUD endpoints and corresponding UI, which conflicts with the requirement to centralize membership management and limit visibility. We need to make BusinessUser read-only (list, view) and restrict visibility to Business owners only. This is a security and product decision impacting both backend and frontend.

Constraints and existing patterns:
- JHipster stack with Spring Data JPA QueryService/Criteria.
- BusinessAuthorizationService provides ownership checks.
- BusinessUserResource contains POST/PUT/PATCH/DELETE/GET endpoints.
- BusinessUserQueryService builds Specifications from BusinessUserCriteria.
- Vue 3 frontend with generated CRUD pages.

Scope:
- Backend: remove/disable write endpoints; enforce owner-only filtering for list and by-id; count reflects same restriction.
- Frontend: hide remove create/edit UI and routes.

## Goals / Non-Goals

Goals:
- Keep only GET list and GET by-id for BusinessUser; disable write operations.
- Only a Business owner can see BusinessUser rows for their Businesses.
- Return 404 for by-id read if current user does not own the associated Business; list shows empty for non-owners.
- Maintain Criteria filtering and pagination for owners.

Non-Goals:
- Changing BusinessUser schema or roles.
- Adding administrative overrides.
- Changing Business create/invite flows.

## Decisions

1) Disable write endpoints by removing mappings
- Rationale: Absence of mappings yields 405 for those HTTP methods; simpler and safer than keeping code and branching with 403.
- Impact: Delete @PostMapping, @PutMapping, @PatchMapping, @DeleteMapping from BusinessUserResource.
- Alternatives: Keep endpoints but return 403; chosen approach avoids public API surface and accidental enablement.

2) Enforce owner-only visibility in QueryService and Service
- Query layer: Add ownership predicate to BusinessUserQueryService.createSpecification:
  - business.owner.id = currentUserId
- Service layer: BusinessUserService.findOne(id) should return Optional.empty() unless current user owns the associated Business.
- Rationale: Ensures list/count are restricted, and direct by-id reads are protected.
- Implementation detail: Resolve current user (UserService) or fall back to login via SecurityUtils as in BusinessQueryService.

3) Frontend changes
- Remove "Create new BusinessUser" buttons and hide/disable create/edit routes for BusinessUser.
- Update navigation where necessary.
- Rationale: Prevents UI entry points to disallowed operations.

4) Testing
- Update existing BusinessUserResourceIT to remove/adjust write-operation tests.
- Add tests to assert owner-only visibility and 404 on non-owner get-by-id; counts reflect only owner-owned Businesses’ members.

5) Optional performance/indexing
- Existing membership index (business_id, user_id) suffices; no additional index needed for owner filter. If needed, consider index on business.owner_id or join path performance tuning later.

## Risks / Trade-offs

- Clients depending on write endpoints break → Mitigation: mark as breaking change, document migration path.
- False positives in ownership resolution if user resolution fails in tests → Mitigation: include login-based fallback; ensure users exist in tests.
- UI confusion if routes remain accessible → Mitigation: remove routes/buttons and add guards.

## Migration Plan

- Backend: remove write endpoints; add owner predicates; adjust service checks.
- Frontend: remove routes/buttons; update tests.
- Rollback: restore endpoints and remove predicates.
- Validation: run integration tests; manual checks with owner vs non-owner users.

## Open Questions

- Should admins (ROLE_ADMIN) bypass owner-only visibility? Default: no bypass here.
- Should owners be able to delete members via another flow? Out of scope in this change; future change can define.
