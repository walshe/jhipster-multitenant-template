# Business Invitation Security Enhancement

## Overview
This specification describes the enhancement to add proper authorization checks to the business invitation endpoints to ensure that only business owners can create, update, or delete invitations for their businesses. Additionally, it clarifies the invitation acceptance and preview flow.

## Requirements

### Functional Requirements
1. Only business owners should be able to create invitations for their businesses
2. Only business owners should be able to update invitations for their businesses
3. Only business owners should be able to delete invitations for their businesses
4. Business owners and members should be able to view invitations for their businesses
5. Anyone with a valid token should be able to view and accept invitations
6. Invitation acceptance flow should be properly secured and validated
7. Invitation preview (viewing by token) should be publicly accessible but read-only
8. The invitedBy field should represent the User who created the invitation
9. The invitedBy field must be set automatically by the backend at creation time
10. The invitedBy field must not be set, modified, or overridden by any client input
11. The invitedBy field is immutable after creation
12. The invitedBy field is informational/audit-only and must not be used for authorization decisions

### Technical Requirements
1. Use Spring Security's `@PreAuthorize` annotations for method-level security
2. Leverage existing security utilities for business ownership checks
3. Maintain backward compatibility with existing functionality
4. Follow JHipster security patterns and best practices
5. Ensure proper validation during invitation acceptance
6. Maintain existing domain model and endpoints without renaming
7. Preserve existing acceptance logic without duplication
8. Implement invitedBy as a many-to-one relationship to the User entity
9. Remove any duplicated audit field that represents the same concept as invitedBy
10. Make invitedBy read-only in DTOs
11. Authorization for CRUD operations is based solely on business ownership, not on invitedBy field

## Implementation Details

### Backend Changes
1. **BusinessInvitationResource.java**:
   - Add `@PreAuthorize` annotation to POST `/businesses/{id}/invitations` endpoint
   - Add `@PreAuthorize` annotation to GET `/businesses/{id}/invitations` endpoint
   - Add `@PreAuthorize` annotation to PUT `/business-invitations/{id}` endpoint
   - Add `@PreAuthorize` annotation to PATCH `/business-invitations/{id}` endpoint
   - Add `@PreAuthorize` annotation to DELETE `/business-invitations/{id}` endpoint
   - Add public endpoint GET `/business-invitations/by-token/{token}` for invitation preview
   - Add authenticated endpoint POST `/business-invitations/accept` for invitation acceptance
   - Ensure invitedBy field is not modifiable via client input in POST, PUT, and PATCH endpoints

2. **BusinessInvitationService.java**:
   - Update service methods to handle Long IDs instead of UUIDs (to match existing DB schema)
   - Ensure proper validation of business ownership before operations
   - Implement secure invitation acceptance flow with proper validation
   - Add method to retrieve invitations by token for preview functionality
   - Automatically set invitedBy field to current user during invitation creation
   - Ensure invitedBy field is immutable after creation
   - Ensure invitedBy field is not used for authorization decisions

3. **BusinessInvitationCriteria.java**:
   - Update ID filter to use StringFilter to accommodate UUID string representations in HTTP requests

4. **BusinessInvitation.java (Entity)**:
   - Remove createdByUserId field
   - Add invitedBy many-to-one relationship to User entity
   - Make invitedBy field required and non-nullable
   - Ensure invitedBy field is set automatically during creation
   - Ensure invitedBy field is immutable after creation

5. **BusinessInvitationDTO.java (DTO)**:
   - Remove createdByUserId field
   - Add invitedBy field as read-only
   - Ensure invitedBy field is not writable via API endpoints

### Security Checks
- Use `@businessSecurity.isBusinessOwner(#businessId)` for business-specific endpoints
- Use `@businessInvitationSecurity.canModifyInvitation(#id)` for invitation-specific endpoints
- Authorization is based solely on business ownership, NOT on the invitedBy field
- Ensure proper validation of user permissions before allowing operations
- Implement secure invitation acceptance flow that verifies:
  - Invitation is in PENDING status
  - Token is valid
  - Authenticated user's email matches invited email

### Invitation Acceptance and Preview Flow
- **Preview by Token**: Anyone with a valid token can view invitation details via GET `/business-invitations/by-token/{token}` (public endpoint)
- **Accept Invitation**: Authenticated users can accept invitations via POST `/business-invitations/accept` with token in request body
- **Validation**: During acceptance, verify that user email matches invited email and invitation is still PENDING
- **Business Relationship Creation**: Upon acceptance, create a BusinessUser relationship between the user and business
- **Status Update**: Set invitation status to ACCEPTED after successful acceptance
- **invitedBy Field**: Represents the user who created the invitation, set automatically, immutable after creation, used for audit purposes only

## Expected Outcomes
- Enhanced security for business invitation management
- Prevention of unauthorized users from creating, updating, or deleting invitations
- Maintained functionality for legitimate business owners
- Proper error responses for unauthorized access attempts
- Clear invitation acceptance and preview flow with appropriate access controls
- Public access to invitation details via token while maintaining security for modification operations
- Proper validation during invitation acceptance to ensure correct user-email matching
- Seamless business-user relationship creation upon successful invitation acceptance
- Automatic setting of invitedBy field to the user creating the invitation
- Immutable invitedBy field after invitation creation
- Audit trail of who created each invitation through the invitedBy field
- Authorization based solely on business ownership, not on who created the invitation
- Proper handling of the invitedBy field in DTOs as read-only
- Removal of redundant createdByUserId field to avoid duplication