## Why

In the current system, authenticated users can list or fetch Businesses they do not belong to. This violates multitenancy isolation and leaks tenant metadata. We need to enforce that users only see Businesses where they are members.

## What Changes

- Enforce membership-based visibility for Business resources:
  - GET /api/businesses: returns only Businesses where the current user is a BusinessUser (owner counts as a member).
  - GET /api/businesses/{id}: returns 404 if the current user is not a member of that Business (to avoid information leakage).
  - GET /api/businesses/count: counts only Businesses visible to the current user.
- Apply filtering at the query layer so all collection queries are tenant-aware by default (append membership Specification based on the authenticated user).
- Guard single-entity reads with a membership check prior to returning data.
- Add/adjust tests to cover membership visibility for list, count, and get-by-id endpoints.

BREAKING
- Non-member users will no longer be able to read Business data they could previously access. Counts and pagination totals will reflect only the caller's memberships.

## Capabilities

### New Capabilities
- `business-visibility-by-membership`: Enforce tenant-aware visibility for Business data so users can only view Businesses for which they have a BusinessUser relationship (including ownership), covering list, count, and get-by-id endpoints.

### Modified Capabilities
- (none)

## Impact

- Backend APIs:
  - BusinessResource: GET /api/businesses, GET /api/businesses/{id}, GET /api/businesses/count will be restricted to the caller's memberships.
  - BusinessQueryService: add membership-aware Specification automatically applied to list and count queries.
  - BusinessService.findOne: add membership check for single-entity fetch.
  - BusinessRepository/BusinessUserRepository: may add helper methods or JPQL/spec predicates to support membership checks.
- Security/Authorization:
  - Use the authenticated user from SecurityContext; treat business owner as an implicit member.
- Tests:
  - Add/modify integration tests covering visibility constraints and 404 behavior for non-members.
- Documentation/OpenAPI:
  - No schema changes; behavior notes to clarify membership visibility.
