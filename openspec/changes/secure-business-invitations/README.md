# Secure Business Invitations

## Description
This change implements proper authorization checks to ensure that only business owners can create, update, or delete invitations for their businesses. It also clarifies and enhances the invitation acceptance and preview flow.

## Problem
Previously, there were no proper authorization checks on the business invitation endpoints, which meant that unauthorized users could potentially create, modify, or delete invitations for businesses they don't own. Additionally, the invitation acceptance and preview flow lacked proper validation and security.

## Solution
Added Spring Security `@PreAuthorize` annotations to all business invitation endpoints to enforce proper authorization checks:
- Only business owners can create invitations for their businesses
- Only business owners can update invitations for their businesses
- Only business owners can delete invitations for their businesses
- Business owners and members can view invitations for their businesses
- Public endpoints for viewing invitations by token remain accessible
- Secure invitation acceptance flow with proper validation

## Key Features
- **Secure Creation**: Only business owners can create invitations for their businesses via `/api/businesses/{id}/invitations`
- **Secure Management**: Only business owners can update or delete invitations
- **Public Preview**: Anyone with a valid token can view invitation details via `/api/business-invitations/by-token/{token}`
- **Secure Acceptance**: Authenticated users can accept invitations via POST to `/api/business-invitations/accept` with proper validation
- **Validation**: During acceptance, verifies that user email matches invited email and invitation is still PENDING
- **Business Relationship**: Creates BusinessUser relationship upon successful invitation acceptance

## Files Modified
- `src/main/java/com/walshe/multitenant/web/rest/BusinessInvitationResource.java`
- `src/main/java/com/walshe/multitenant/service/BusinessInvitationService.java`
- `src/main/java/com/walshe/multitenant/service/criteria/BusinessInvitationCriteria.java`
- Test files to accommodate ID type changes and security enhancements

## Testing
All existing tests continue to pass, confirming that the functionality is preserved while adding the necessary security checks.