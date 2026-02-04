## Why

BusinessUser management is currently exposed with full CRUD operations and UI affordances (create/edit). For multitenancy and governance, BusinessUser should be read-only from this UI/API, and only the Business owner should be able to view membership details. Members and non-members must not read BusinessUser rows unless they own the Business.

## What Changes

Backend
- Disable create/update/patch/delete APIs for BusinessUser; only allow list (GET /api/business-users) and view (GET /api/business-users/{id}).
- Enforce owner-only visibility for BusinessUser list/view:
  - GET /api/business-users: only return BusinessUser rows whose Business is owned by the current user.
  - GET /api/business-users/{id}: return 404 unless the current user owns the associated Business.
- Update authorization checks (service/query) to apply ownership constraints consistently.

Frontend
- Remove/hide "Create new BusinessUser" buttons.
- Ensure only list and detail pages are available; remove/disable routes for create/edit BusinessUser.
- Update texts to clarify owner-only access where appropriate.

Tests
- Update/remove tests that cover write operations for BusinessUser.
- Add tests verifying owner-only visibility and 404 semantics for non-owners on BusinessUser GET by id.

BREAKING
- Removing BusinessUser write endpoints and UI controls is a breaking behavior for clients relying on them. Only read operations remain, and reads are restricted to Business owners.

## Capabilities

### New Capabilities
- `business-user-read-only`: BusinessUser is read-only in API and UI (list and view only; no create/update/delete).
- `business-user-owner-only-visibility`: Only the Business owner can list or view BusinessUser entries; non-owners receive empty lists or 404.

### Modified Capabilities
- (none)

## Impact

- Backend APIs: BusinessUserResource - remove or disable POST/PUT/PATCH/DELETE; restrict GET list and GET by id with owner-only checks.
- Services/Query: BusinessUserQueryService - append ownership predicate; BusinessUserService - enforce owner check on by-id; optionally add repository helpers.
- Frontend: Remove create/edit routes and buttons for BusinessUser; ensure only list/detail pages exist.
- Security: Rely on current authenticated user; enforce owner-only access pattern.
- Documentation: Update API docs to reflect read-only status and owner-only visibility.
