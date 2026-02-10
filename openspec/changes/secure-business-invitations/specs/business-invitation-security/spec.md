# Business Invitation Security Enhancement

## Overview
This specification enhances authorization on business invitation endpoints to ensure only business owners can create, update, or delete invitations for their businesses. It also defines a correct invitation preview and acceptance flow that supports both unregistered invitees and existing users.

This spec intentionally separates:
- **Invitation Preview** (public, read-only, no side effects)
- **Invitation Acceptance** (authenticated, explicit action, validated)

## Requirements

### Functional Requirements

1. Only **Business Owners** (users who are `business.owner`)
   may create invitations for their businesses.

2. Only **Business Owners** may update invitations for their
   businesses, and only while the invitation status is `PENDING`.

3. Only **Business Owners** may delete invitations for their
   businesses, and only while the invitation status is `PENDING`.

4. **Business Owners** may view all invitations for their businesses.

5. **Business Members** must not be able to view all invitations
   for a business.
   - If invitation visibility is provided to members at all,
     it must be limited strictly to invitations that directly
     relate to them (e.g. where `invitedEmail` matches the
     member’s email).
   - By default, invitation listing is an owner-only capability.

6. Anyone with a valid token may **preview** an invitation
   in a read-only manner.

7. Invitation preview (viewing by token) must be publicly
   accessible, read-only, and must not accept or mutate
   the invitation.

8. Invitation acceptance must be explicitly initiated by the user
   and must be properly secured and validated.

9. The `invitedBy` field represents the User who created
   the invitation.

10. `invitedBy` must be set automatically by the backend
    at creation time.

11. `invitedBy` must not be set, modified, or overridden
    by any client input.

12. `invitedBy` is immutable after creation.

13. `invitedBy` is informational/audit-only and must not be
    used for authorization decisions.


## API Rules

This section defines authorization and visibility rules at the API level.
These rules are normative and must be enforced by all implementations.

### Invitation Creation
- Endpoint(s) that create BusinessInvitations must:
  - Require authentication.
  - Verify that the authenticated user is the **Business Owner**
    (`business.owner`) of the referenced Business.
- Invitation creation requests must ignore any client-supplied value
  for `invitedBy`; this field is set by the backend only.

### Invitation Update
- Endpoints that update BusinessInvitations must:
  - Require authentication.
  - Verify that the authenticated user is the **Business Owner**
    of the associated Business.
  - Reject updates if the invitation status is not `PENDING`.
  - Allow modification of the `role` field only.
  - Reject any attempt to modify immutable fields
    (`token`, `invitedEmail`, `invitedBy`, `status`, `businessId`).

### Invitation Deletion
- Endpoints that delete BusinessInvitations must:
  - Require authentication.
  - Verify that the authenticated user is the **Business Owner**
    of the associated Business.
  - Reject deletion if the invitation status is not `PENDING`.

### Invitation Listing
- Endpoints that list invitations for a Business must:
  - Require authentication.
  - Allow **Business Owners** to view all invitations
    for businesses they own.
  - Must NOT allow Business Members to list all invitations
    for a business they do not own.
- If invitation visibility is provided to Business Members at all:
  - It must be limited strictly to invitations that directly
    relate to the authenticated member
    (e.g. `invitedEmail` matches the user’s email).
  - This capability is optional and not required by default.

### Invitation Preview by Token
- The invitation preview endpoint
  (`GET /api/business-invitations/by-token/{token}`):
  - Must be publicly accessible (no authentication required).
  - Must be strictly read-only.
  - Must not accept, mutate, or otherwise change invitation state.
  - Must not create any authenticated security context.

### Invitation Acceptance
- The invitation acceptance endpoint
  (`POST /api/business-invitations/accept`) must:
  - Require authentication.
  - Validate that the invitation exists and is in `PENDING` status.
  - Validate that the provided token is valid.
  - Validate that the authenticated user’s email matches
    the invitation’s `invitedEmail`.
  - Create a BusinessUser relationship upon successful acceptance.
  - Transition the invitation status to `ACCEPTED`.
  - Reject acceptance if any validation fails.

### invitedBy Field Rules
- The `invitedBy` field:
  - Must be set automatically by the backend at creation time.
  - Must be immutable after creation.
  - Must be exposed as read-only in any API responses or DTOs.
  - Must not be used as an authorization decision input.



### Technical Requirements
1. Enforce method-level security using Spring Security (e.g., `@PreAuthorize`) and/or a centralized security service.
2. Leverage existing business ownership / membership checks.
3. Keep existing endpoints and domain model stable (no endpoint renames unless unavoidable).
4. Follow JHipster security patterns and best practices.
5. Ensure proper validation during invitation acceptance.
6. Preserve existing acceptance logic without duplicating it.
7. Implement `invitedBy` as a many-to-one relationship to `User`.
8. Remove any duplicated audit field representing the same concept as `invitedBy` (e.g., remove `createdByUserId`).
9. `invitedBy` must be read-only in DTOs and not writable via POST/PUT/PATCH.
10. Authorization for CRUD operations is based solely on **business ownership** (and, where applicable, business membership for read-only listing), not on `invitedBy`.

## Invitation Preview and Acceptance Flow (Authoritative)

### A) Invitation Link Format (What gets copied/shared)
- The invitation link must point to a **public preview route**, not directly to registration.
- Example frontend route:
  - `http://localhost:8080/invite/{token}`

Notes:
- The preview route is a UI concern, but the backend must support it via the public token lookup endpoint below.
- **No email is sent** as part of this flow (out of scope).

### B) Invitation Preview (Public, Read-only)
- Endpoint: `GET /api/business-invitations/by-token/{token}`
- Access: **Public** (no authentication required)
- Behavior:
  - Returns invitation details (business name/id, invitedEmail, role, status).
  - Must be **read-only**.
  - Must have **no side effects** (must not accept, mutate, or log in a user).

### C) Invitation Acceptance (Authenticated, Explicit Action)
- Endpoint: `POST /api/business-invitations/accept`
- Access: **Authenticated**
- Request body:
  - `{ "token": "..." }`
- Validation rules:
  1. Invitation exists for token
  2. Invitation status is `PENDING`
  3. Token is valid
  4. Authenticated user exists
  5. **Authenticated user email must exactly match `invitedEmail`**
     - If mismatch: reject with 403/400 (implementation choice), do not accept.
- Behavior upon successful acceptance:
  - Create `BusinessUser` membership:
    - business = invitation.business
    - user = authenticated user
    - role = invitation.role
  - Set invitation status to `ACCEPTED`
  - Make invitation immutable thereafter

### D) Unregistered Invitees
- Unregistered users must be able to preview the invitation via the public token endpoint.
- Acceptance still requires authentication.
- If the user is not authenticated when they click “Accept” in the UI:
  - the UI redirects to login/registration
  - after authentication, the UI resumes acceptance using the original token

## Authorization Rules (CRUD + Listing)

### Owner-only operations
- Create invitation: owner of business only
- Update invitation: owner of business only, PENDING only (role only)
- Delete invitation: owner of business only, PENDING only

### Read/list operations
- List invitations for a business:
  - Owners and members may view invitations for their business (read-only list).
  - Must be scoped to that business (no global list).

### Token access
- Public by-token preview is allowed for anyone with a valid token.
- Token access must not grant any other privileges.

## Implementation Details (Non-Authoritative Guidance)

### Backend Changes (High-level)
1. **BusinessInvitationResource**
   - Secure business-scoped endpoints with business ownership/membership checks:
     - `POST /api/businesses/{businessId}/invitations` → owner only
     - `GET /api/businesses/{businessId}/invitations` → owner or member (read-only)
   - Secure invitation mutation endpoints:
     - `PUT/PATCH/DELETE /api/business-invitations/{id}` → owner only + PENDING constraints
   - Add/keep:
     - `GET /api/business-invitations/by-token/{token}` → public preview
     - `POST /api/business-invitations/accept` → authenticated acceptance
   - Ensure `invitedBy` cannot be set/changed via client input.

2. **BusinessInvitationService**
   - Enforce ownership before create/update/delete.
   - Enforce PENDING-only update/delete rules and role-only mutation.
   - Implement acceptance:
     - validate token + status
     - validate email match
     - create BusinessUser
     - set status ACCEPTED

3. **Entity / DTO**
   - `BusinessInvitation`:
     - remove `createdByUserId`
     - add `invitedBy` many-to-one to `User`, non-null
   - `BusinessInvitationDTO`:
     - include `invitedBy` as read-only (or expose `invitedBy.id/login` read-only)
     - do not accept `invitedBy` in requests

### Security Checks (examples)
- Business-scoped:
  - `@businessSecurity.isBusinessOwner(#businessId)` for create
  - `@businessSecurity.isBusinessOwnerOrMember(#businessId)` for list
- Invitation-scoped:
  - `@businessInvitationSecurity.canModifyInvitation(#id)` should resolve business ownership by invitation->business mapping

## Expected Outcomes
- Unauthorized users cannot create/update/delete invitations.
- Owners and members can list invitations scoped to their business.
- Anyone with a token can preview the invitation without authentication.
- Acceptance is secure and explicit:
  - requires authentication
  - requires email match
  - requires PENDING status
- Invitations record who created them via `invitedBy`.
- `invitedBy` is backend-managed, immutable, and audit-only.
- `createdByUserId` is removed to avoid duplication.
