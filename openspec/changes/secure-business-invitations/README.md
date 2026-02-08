# Secure Business Invitations

## Description
This change implements proper authorization checks to ensure that only business owners can create, update, or delete invitations for their businesses.

## Problem
Previously, there were no proper authorization checks on the business invitation endpoints, which meant that unauthorized users could potentially create, modify, or delete invitations for businesses they don't own.

## Solution
Added Spring Security `@PreAuthorize` annotations to all business invitation endpoints to enforce proper authorization checks:
- Only business owners can create invitations for their businesses
- Only business owners can update invitations for their businesses
- Only business owners can delete invitations for their businesses
- Business owners and members can view invitations for their businesses
- Public endpoints for viewing and accepting invitations by token remain accessible

## Files Modified
- `src/main/java/com/walshe/multitenant/web/rest/BusinessInvitationResource.java`
- `src/main/java/com/walshe/multitenant/service/BusinessInvitationService.java`
- `src/main/java/com/walshe/multitenant/service/criteria/BusinessInvitationCriteria.java`
- Test files to accommodate UUID changes

## Testing
All existing tests continue to pass, confirming that the functionality is preserved while adding the necessary security checks.