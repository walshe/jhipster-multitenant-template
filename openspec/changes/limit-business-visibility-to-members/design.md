## Context

The application is a JHipster-generated Spring Boot service evolving toward multitenancy. The Business domain is central and has a membership relationship via BusinessUser to associate Users to Businesses. Authorization is incomplete: currently, authenticated users can list or fetch Businesses they do not belong to. We must enforce tenant-aware visibility.

Constraints and existing patterns:
- Tech stack: Spring Boot, Spring Security, Spring Data JPA (QueryService + Criteria + Specification), MySQL/H2, Vue frontend.
- Existing classes: BusinessResource, BusinessService, BusinessQueryService, BusinessAuthorizationService, BusinessRepository, BusinessUserRepository, BusinessCriteria.
- DTO/Enum: BusinessRole contains only MEMBER (normalized in DB). No role-specific branching is needed for reads.
- Prefer minimal, non-invasive changes aligned with JHipster’s QueryService pattern.

Scope of this change:
- Enforce membership-based visibility for Business list, count, and single-entity GET. Owners are implicit members.
- No schema changes required; behavior-only change at the query and service layers.

## Goals / Non-Goals

Goals:
- Restrict GET /api/businesses and GET /api/businesses/count to Businesses where the current user is a member or owner.
- Return 404 (not found) for GET /api/businesses/{id} when the current user is not a member/owner, to avoid information leakage.
- Centralize and reuse the membership predicate to ensure consistent enforcement across list and count queries.
- Maintain compatibility with existing JHipster Criteria/Specification filtering and eager relationship fetching.

Non-Goals:
- Introduce new roles/permissions beyond MEMBER-only read semantics.
- Change write behaviors (create/update/delete) or membership management flows.
- Modify API shapes or OpenAPI schemas.
- Implement admin/global override rules (can be a follow-up decision if needed).

## Decisions

1) Apply membership filtering via JPA Specification in BusinessQueryService
- Rationale: Centralizes tenant-aware filtering at the standard query entry point used by both list and count; minimal changes to controllers; aligns with JHipster pattern.
- Approach:
  - In createSpecification(criteria), append a predicate that restricts results to the authenticated user’s memberships OR ownership.
  - Specification logic (conceptual):
    - business.owner.id = :currentUserId
    - OR EXISTS BusinessUser bu WHERE bu.business.id = business.id AND bu.user.id = :currentUserId
  - Ensure distinct(true) when joins are present to prevent duplicate rows.
  - Continue honoring any incoming BusinessCriteria filters (combine with AND around the membership OR-clause).

2) Guard single-entity reads in BusinessService.findOne
- Rationale: Even with list/count filtering, direct GET by id could leak data if not checked. Service-layer guard keeps BusinessResource simple and consistent.
- Approach:
  - On findOne(id), verify membership or ownership using BusinessAuthorizationService (or a repository-based check) before returning the entity.
  - If not a member/owner, return Optional.empty(); BusinessResource will respond with 404.
- Decision: Use 404 (Not Found) instead of 403 (Forbidden) to avoid disclosing existence of other tenants’ resources.

3) Use BusinessAuthorizationService for membership checks
- Rationale: Keeps security logic in one place; encapsulates SecurityContext principal resolution and repository access.
- Implementation details:
  - Add/confirm methods like isCurrentUserBusinessMemberOrOwner(Long businessId).
  - Internally, resolve current user id from SecurityContext and check either ownership match or existence of BusinessUser(businessId, userId).

4) Repository and performance considerations
- Add a reusable Specification builder method in BusinessQueryService (or a separate Spec utility) that builds the membership-or-ownership predicate given a user id.
- Set query distinct to avoid duplicates from joins.
- Indexing (optional optimization): ensure an index on business_user(business_id, user_id) exists for efficient lookups. If missing, plan a follow-up Liquibase changeset. Not required to deliver this change.

5) Eager relationship fetching
- Continue to use existing findAllWithToOneRelationships/findOneWithToOneRelationships where applicable. When applying Specifications, prefer the standard repository method findAll(spec, pageable) with distinct handling; avoid breaking existing mapping behavior.

6) Testing strategy
- Integration tests to cover:
  - Non-member cannot list Businesses; receives empty list; count is 0.
  - Member can list and count only their Businesses.
  - Non-member GET /api/businesses/{id} returns 404; member/owner receives 200 and entity body.
  - Owner implicitly treated as member.
  - Criteria filters still work in combination with membership restriction.

## Risks / Trade-offs

- Duplicate rows due to joins → Mitigation: enforce distinct on queries when the membership join is present.
- Performance impact for large datasets due to EXISTS/joins → Mitigation: ensure composite index on business_user(business_id, user_id); validate query plans; add pagination tests.
- Security principal resolution failures (e.g., unauthenticated requests) → Mitigation: controller methods already require authentication; double-check security annotations.
- Inconsistent enforcement if future endpoints bypass QueryService → Mitigation: add code comments and tests; consider a reusable Specification helper method referenced by all query paths; enforce service-layer check for single-entity reads.
- 404 vs 403 semantics may surprise clients → Mitigation: document behavior in API descriptions and release notes.

## Migration Plan

- Deployment: code-only change. No DB migration required.
- Optional follow-up: add Liquibase changeset to create index on business_user(business_id, user_id) if not present for performance.
- Rollback: revert code changes; no data migration needed.
- Validation after deploy:
  - Smoke test login flows.
  - Verify list/count endpoints return only caller-owned/ member Businesses.
  - Verify 404 on GET by id for non-member.

## Open Questions

- Admin visibility: Should users with an ADMIN authority bypass membership checks? Default here: no bypass. If needed, add a role-based override condition.
- Multi-relationship semantics: Do pending invitations count as membership? Default: only accepted BusinessUser records and ownership count.
- Indexing: Should we add the composite index now or defer until performance demands it?
- Centralization: Should the membership Specification be factored into a dedicated Specification utility class for reuse beyond Business?
