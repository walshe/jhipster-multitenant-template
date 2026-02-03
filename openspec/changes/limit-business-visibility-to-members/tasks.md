## 1. Preparation

- [ ] 1.1 Review BusinessResource, BusinessService, BusinessQueryService to confirm current query flow for list, count, and get-by-id
- [ ] 1.2 Identify method to obtain current user id (e.g., via BusinessAuthorizationService or Security utilities) and confirm availability throughout services
- [ ] 1.3 Verify BusinessUserRepository has lookups by (businessId, userId); add exists-style method if needed for efficient checks

## 2. Query-layer membership filtering (list and count)

- [ ] 2.1 Add a private helper in BusinessQueryService to build a membership-or-ownership Specification for the current user
- [ ] 2.2 Update createSpecification(criteria) to AND the membership Specification with existing criteria when the caller is authenticated
- [ ] 2.3 Ensure distinct(true) is applied when membership join is added to avoid duplicates
- [ ] 2.4 Validate that pagination and sorting still function as expected with the new Specification

## 3. Single-entity GET protection

- [ ] 3.1 Add method in BusinessAuthorizationService: isCurrentUserBusinessMemberOrOwner(Long businessId)
- [ ] 3.2 In BusinessService.findOne(Long id), short-circuit: return Optional.empty() if not a member/owner; otherwise fetch and return
- [ ] 3.3 Confirm BusinessResource translates Optional.empty() to 404 (Not Found)

## 4. Repository adjustments (if needed)

- [ ] 4.1 Add BusinessUserRepository.existsByBusinessIdAndUserId(Long businessId, Long userId) for efficient membership checks (if not already present)
- [ ] 4.2 Ensure BusinessRepository methods used for findOne do not bypass service-layer authorization

## 5. Tests

- [ ] 5.1 Add integration test: non-member GET /api/businesses returns empty list and count=0
- [ ] 5.2 Add integration test: member GET /api/businesses returns only their Businesses; count matches
- [ ] 5.3 Add integration test: GET /api/businesses/{id} returns 404 for non-member
- [ ] 5.4 Add integration test: GET /api/businesses/{id} returns 200 for member and owner
- [ ] 5.5 Add integration test: criteria filters combine with membership restriction (e.g., name.contains)

## 6. Documentation

- [ ] 6.1 Update API docs or controller JavaDoc to note membership-based visibility and 404 semantics
- [ ] 6.2 Add release note summarizing behavioral change (visibility restricted to memberships)

## 7. Optional performance follow-up

- [ ] 7.1 Evaluate query plans; if needed, add Liquibase changeset for composite index on business_user(business_id, user_id)
- [ ] 7.2 Load-test list/count endpoints with realistic data volumes

## 8. Manual verification

- [ ] 8.1 Start application and log in as: (a) non-member user, (b) member, (c) owner; exercise list, count, and get-by-id
- [ ] 8.2 Confirm non-member receives 404 for any business id (including existing ones)
