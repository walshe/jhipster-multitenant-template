# Business Invitation Security Enhancement

## Overview
This specification describes the enhancement to add proper authorization checks to the business invitation endpoints to ensure that only business owners can create, update, or delete invitations for their businesses.

## Requirements

### Functional Requirements
1. Only business owners should be able to create invitations for their businesses
2. Only business owners should be able to update invitations for their businesses
3. Only business owners should be able to delete invitations for their businesses
4. Business owners and members should be able to view invitations for their businesses
5. Anyone with a valid token should be able to view and accept invitations

### Technical Requirements
1. Use Spring Security's `@PreAuthorize` annotations for method-level security
2. Leverage existing security utilities for business ownership checks
3. Maintain backward compatibility with existing functionality
4. Follow JHipster security patterns and best practices

## Implementation Details

### Backend Changes
1. **BusinessInvitationResource.java**:
   - Add `@PreAuthorize` annotation to POST `/businesses/{id}/invitations` endpoint
   - Add `@PreAuthorize` annotation to GET `/businesses/{id}/invitations` endpoint
   - Add `@PreAuthorize` annotation to PUT `/business-invitations/{id}` endpoint
   - Add `@PreAuthorize` annotation to PATCH `/business-invitations/{id}` endpoint
   - Add `@PreAuthorize` annotation to DELETE `/business-invitations/{id}` endpoint

2. **BusinessInvitationService.java**:
   - Update service methods to handle UUID IDs instead of Long
   - Ensure proper validation of business ownership before operations

3. **BusinessInvitationCriteria.java**:
   - Update ID filter to use StringFilter to accommodate UUIDs in HTTP requests

### Security Checks
- Use `@businessSecurity.isBusinessOwner(#businessId)` for business-specific endpoints
- Use `@businessInvitationSecurity.canModifyInvitation(#id)` for invitation-specific endpoints
- Ensure proper validation of user permissions before allowing operations

## Expected Outcomes
- Enhanced security for business invitation management
- Prevention of unauthorized users from creating, updating, or deleting invitations
- Maintained functionality for legitimate business owners
- Proper error responses for unauthorized access attempts