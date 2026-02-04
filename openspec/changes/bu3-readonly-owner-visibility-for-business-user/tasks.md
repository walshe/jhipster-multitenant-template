## 1. Preparation

- [ ] 1.1 Review BusinessUserResource and identify POST/PUT/PATCH/DELETE endpoints to remove
- [ ] 1.2 Review BusinessUserQueryService and BusinessUserService to plan owner-only filtering and by-id guard
- [ ] 1.3 Verify how to resolve current user (UserService and SecurityUtils fallback) as in BusinessQueryService

## 2. Backend API changes (read-only)

- [ ] 2.1 Remove or disable POST /api/business-users endpoint
- [ ] 2.2 Remove or disable PUT /api/business-users/{id} endpoint
- [ ] 2.3 Remove or disable PATCH /api/business-users/{id} endpoint
- [ ] 2.4 Remove or disable DELETE /api/business-users/{id} endpoint
- [ ] 2.5 Ensure only GET list and GET by-id mappings remain in BusinessUserResource

## 3. Owner-only visibility enforcement

- [ ] 3.1 In BusinessUserQueryService.createSpecification, AND a predicate requiring business.owner.id == currentUserId (or login fallback)
- [ ] 3.2 In BusinessUserService.findOne(Long id), return Optional.empty() unless current user owns the associated Business
- [ ] 3.3 Ensure GET /api/business-users/count reflects owner-only constraint

## 4. Tests (backend)

- [ ] 4.1 Remove/disable tests asserting BusinessUser create/update/patch/delete behaviors
- [ ] 4.2 Add integration test: owner can list members of their Businesses; count reflects members
- [ ] 4.3 Add integration test: non-owner list is empty; count is 0
- [ ] 4.4 Add integration test: GET /api/business-users/{id} returns 404 for non-owner, 200 for owner

## 5. Frontend (Vue)

- [ ] 5.1 Remove "Create new BusinessUser" buttons from BusinessUser list view
- [ ] 5.2 Remove/disable routes/views for BusinessUser create/edit
- [ ] 5.3 Ensure only list and detail pages remain for BusinessUser

## 6. Documentation & Changelog

- [ ] 6.1 Update API docs (JavaDoc or OpenAPI descriptions) to state read-only and owner-only visibility
- [ ] 6.2 Add release note: BusinessUser is read-only; only owners can view/list members; write endpoints removed

## 7. Manual verification

- [ ] 7.1 Start app and verify POST/PUT/PATCH/DELETE to /api/business-users are not mapped (405)
- [ ] 7.2 Verify owner vs non-owner behaviors for list/count/get-by-id
